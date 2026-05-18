package org.sopt.app.application.soptamp;

import static org.sopt.app.domain.entity.soptamp.SoptampUser.createNewSoptampUser;
import static org.sopt.app.domain.enums.SoptPart.findSoptPartByPartName;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.platform.dto.PlatformUserInfoResponse;
import org.sopt.app.application.rank.RankCacheService;
import org.sopt.app.application.soptamp.SoptampEvent.SoptampUserAllCacheSyncEvent;
import org.sopt.app.application.soptamp.SoptampEvent.SoptampUserProfileCacheSyncEvent;
import org.sopt.app.application.soptamp.SoptampEvent.SoptampUserScoreCacheSyncEvent;
import org.sopt.app.application.user.UserWithdrawEvent;
import org.sopt.app.common.event.EventPublisher;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.AppjamUser;
import org.sopt.app.domain.entity.soptamp.SoptampUser;
import org.sopt.app.domain.enums.SoptPart;
import org.sopt.app.interfaces.postgres.AppjamUserRepository;
import org.sopt.app.interfaces.postgres.SoptampUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SoptampUserService {

    private final SoptampUserRepository soptampUserRepository;
    private final AppjamUserRepository appjamUserRepository;
    private final RankCacheService rankCacheService;
    private final EventPublisher eventPublisher;

    @Value("${makers.app.soptamp.appjam-mode:false}")
    private boolean appjamMode;

    @Value("${sopt.current.generation}")
    private Long currentGeneration;

    @Transactional(readOnly = true)
    public SoptampUserInfo getSoptampUserInfo(Long userId) {
        SoptampUser user = soptampUserRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        return SoptampUserInfo.of(user);
    }

    @Transactional
    public SoptampUserInfo editProfileMessage(Long userId, String profileMessage) {
        SoptampUser soptampUser = soptampUserRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        soptampUser.updateProfileMessage(profileMessage);
        this.raiseProfileCacheSyncEvent(soptampUser);
        return SoptampUserInfo.of(soptampUser);
    }

    @Transactional(readOnly = true)
    public List<Long> getUpsertTargetUserIds() {
        return soptampUserRepository.findAllUserIds();
    }

    @Transactional
    public void upsertAllSoptampUsers(Map<Long, PlatformUserInfoResponse> profileMap) {
        if (profileMap.isEmpty()) return;

        Map<Long, SoptampUser> existingUserMap = soptampUserRepository
            .findAllByUserIdIn(profileMap.keySet()).stream()
            .collect(Collectors.toMap(SoptampUser::getUserId, Function.identity()));

        Map<Long, AppjamUser> appjamUserMap = appjamMode
            ? appjamUserRepository.findAllByUserIdIn(profileMap.keySet()).stream()
                .collect(Collectors.toMap(AppjamUser::getUserId, Function.identity(), (a, b) -> a))
            : Map.of();

        Set<String> reservedNicknames = new HashSet<>();

        profileMap.forEach((userId, profile) ->
            upsertSoptampUserCore(profile, userId, appjamUserMap, existingUserMap, reservedNicknames));
    }

    @Transactional
    public void upsertSoptampUser(PlatformUserInfoResponse profile, Long userId) {
        if (profile == null) return;
        if (!appjamMode && profile.getLatestSoptActivity() == null) return;

        Map<Long, SoptampUser> existingUserMap = soptampUserRepository.findByUserId(userId)
            .map(u -> Map.of(userId, u))
            .orElse(Map.of());
        upsertSoptampUserCore(profile, userId, null, existingUserMap, new HashSet<>());
    }

    private void upsertSoptampUserCore(PlatformUserInfoResponse profile, Long userId,
            Map<Long, AppjamUser> appjamUserMap,
            Map<Long, SoptampUser> existingUserMap,
            Set<String> reservedNicknames) {
        if (profile == null) return;

        if (appjamMode) {
            upsertSoptampUserForAppjam(profile, userId, profile.getLatestActivity(),
                appjamUserMap, existingUserMap, reservedNicknames);
        } else {
            var latestSopt = profile.getLatestSoptActivity();
            if (latestSopt == null) return;
            upsertSoptampUserNormal(profile, userId, latestSopt, existingUserMap, reservedNicknames);
        }
    }

    private void upsertSoptampUserNormal(PlatformUserInfoResponse profile, Long userId,
            PlatformUserInfoResponse.SoptActivities latest,
            Map<Long, SoptampUser> existingUserMap,
            Set<String> reservedNicknames) {
        SoptampUser registeredUser = existingUserMap.get(userId);
        if (registeredUser == null) {
            this.createSoptampUserNormal(profile, userId, latest, reservedNicknames);
            return;
        }
        if (this.isGenerationChanged(registeredUser, (long) profile.lastGeneration())) {
            updateSoptampUserNormal(registeredUser, profile, latest, reservedNicknames);
        }
    }

    private void updateSoptampUserNormal(SoptampUser registeredUser, PlatformUserInfoResponse profile,
            PlatformUserInfoResponse.SoptActivities latest, Set<String> reservedNicknames) {
        String part = latest.part() == null ? "미상" : latest.part();
        String newNickname = generatePartBasedUniqueNickname(profile.name(), part, registeredUser.getUserId(), reservedNicknames);

        registeredUser.initTotalPoints();
        registeredUser.updateChangedGenerationInfo(
                (long) profile.lastGeneration(),
                findSoptPartByPartName(part),
            newNickname
        );

        this.raiseAllCacheSyncEvent(registeredUser);
    }

    private void createSoptampUserNormal(PlatformUserInfoResponse profile, Long userId,
        PlatformUserInfoResponse.SoptActivities latestSopt, Set<String> reservedNicknames
    ) {
        String part = latestSopt.part() == null ? "미상" : latestSopt.part();
        String uniqueNickname = generatePartBasedUniqueNickname(profile.name(), part, null, reservedNicknames);
        SoptampUser newSoptampUser = createNewSoptampUser(userId, uniqueNickname, (long) profile.lastGeneration(),
                findSoptPartByPartName(part));
        soptampUserRepository.save(newSoptampUser);
        this.raiseAllCacheSyncEvent(newSoptampUser);
    }

    private boolean isGenerationChanged(SoptampUser registeredUser, Long profileGeneration) {
        return !registeredUser.getGeneration().equals(profileGeneration);
    }

    private void upsertSoptampUserForAppjam(PlatformUserInfoResponse profile,
            Long userId,
            PlatformUserInfoResponse.SoptActivities latest,
            Map<Long, AppjamUser> appjamUserMap,
            Map<Long, SoptampUser> existingUserMap,
            Set<String> reservedNicknames) {
        SoptampUser registeredUser = existingUserMap.get(userId);

        if (registeredUser == null) {
            createSoptampUserAppjam(profile, userId, latest, appjamUserMap, reservedNicknames);
            return;
        }

        if (!needsAppjamNicknameMigration(registeredUser)) {
            return;
        }

        String baseNickname = buildAppjamBaseNickname(profile, userId, appjamUserMap);
        String uniqueNickname = generateUniqueNicknameInternal(baseNickname, userId, reservedNicknames);

        String part = (latest == null || latest.part() == null) ? "미상" : latest.part();
        registeredUser.updateChangedGenerationInfo(
                (long) profile.lastGeneration(),
                findSoptPartByPartName(part),
                uniqueNickname
            );

        registeredUser.initTotalPoints();
        this.raiseAllCacheSyncEvent(registeredUser);
    }

    private void createSoptampUserAppjam(PlatformUserInfoResponse profile,
            Long userId,
            PlatformUserInfoResponse.SoptActivities latest,
            Map<Long, AppjamUser> appjamUserMap,
            Set<String> reservedNicknames
    ) {
        String baseNickname = buildAppjamBaseNickname(profile, userId, appjamUserMap);
        String uniqueNickname = generateUniqueNicknameInternal(baseNickname, null, reservedNicknames);

        String part = (latest == null || latest.part() == null) ? "미상" : latest.part();

        SoptampUser newSoptampUser = createNewSoptampUser(
                userId,
                uniqueNickname,
                (long) profile.lastGeneration(),
                findSoptPartByPartName(part));
        newSoptampUser.initTotalPoints();

        soptampUserRepository.save(newSoptampUser);
        this.raiseAllCacheSyncEvent(newSoptampUser);
    }

    private boolean needsAppjamNicknameMigration(SoptampUser user) {
        String nickname = user.getNickname();
        if (nickname == null || nickname.isBlank()) {
            return true;
        }

        for (SoptPart part : SoptPart.values()) {
            if (!part.isSoptPart())
                continue;
            String prefix = part.getShortedPartName();
            if (nickname.startsWith(prefix)) {
                return true;
            }
        }

        return false;
    }

    private String buildAppjamBaseNickname(PlatformUserInfoResponse profile, Long userId,
            Map<Long, AppjamUser> appjamUserMap) {
        Optional<AppjamUser> appjamUser = (appjamUserMap != null)
            ? Optional.ofNullable(appjamUserMap.get(userId))
            : appjamUserRepository.findByUserId(userId);
        return appjamUser
            .map(u -> u.getTeamName() + profile.name())
            .orElseGet(() -> profile.lastGeneration() + "기" + profile.name());
    }

    private static final String SUFFIX_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private String generatePartBasedUniqueNickname(String name, String part, Long currentUserIdOrNull,
            Set<String> reservedNicknames) {
        String prefixPartName = SoptPart.findSoptPartByPartName(part).getShortedPartName();
        String baseNickname = prefixPartName + name;
        return generateUniqueNicknameInternal(baseNickname, currentUserIdOrNull, reservedNicknames);
    }

    private String generateUniqueNicknameInternal(String baseNickname, Long currentUserIdOrNull,
            Set<String> reservedNicknames) {
        if (!existsNickname(baseNickname, currentUserIdOrNull) && !reservedNicknames.contains(baseNickname)) {
            reservedNicknames.add(baseNickname);
            return baseNickname;
        }

        for (int i = 0; i < SUFFIX_CHARS.length(); i++) {
            String candidate = baseNickname + SUFFIX_CHARS.charAt(i);
            if (!existsNickname(candidate, currentUserIdOrNull) && !reservedNicknames.contains(candidate)) {
                reservedNicknames.add(candidate);
                return candidate;
            }
        }
        throw new BadRequestException(ErrorCode.NICKNAME_IS_FULL);
    }

    private boolean existsNickname(String nickname, Long currentUserIdOrNull) {
        if (currentUserIdOrNull == null) {
            return soptampUserRepository.existsByNickname(nickname);
        }
        return soptampUserRepository.existsByNicknameAndUserIdNot(nickname, currentUserIdOrNull);
    }

    @Transactional
    public void addPointByLevel(Long userId, Integer level) {
        SoptampUser soptampUser = soptampUserRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        soptampUser.addPointsByLevel(level);

        this.raiseScoreCacheSyncEvent(soptampUser);
    }

    @Transactional
    public void subtractPointByLevel(Long userId, Integer level) {
        SoptampUser soptampUser = soptampUserRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        soptampUser.subtractPointsByLevel(level);

        this.raiseScoreCacheSyncEvent(soptampUser);
    }

    @Transactional
    public void initPoint(Long userId) {
        SoptampUser soptampUser = soptampUserRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        soptampUser.initTotalPoints();
        soptampUserRepository.save(soptampUser);
        this.raiseScoreCacheSyncEvent(soptampUser);
    }

    @Transactional
    public void initAllSoptampUserPoints() {
        List<SoptampUser> soptampUserList = soptampUserRepository.findAll();
        soptampUserList.forEach(SoptampUser::initTotalPoints);
        soptampUserRepository.saveAll(soptampUserList);
        if (!appjamMode) {
            rankCacheService.deleteAll();
            List<SoptampUserInfo> currentGenerationUserInfos = soptampUserList.stream()
                .filter(u -> currentGeneration.equals(u.getGeneration()))
                .map(SoptampUserInfo::of)
                .toList();
            rankCacheService.addAll(currentGenerationUserInfos);
        }
    }

    @Transactional
    public void initSoptampRankCache() {
        if (appjamMode) {
            throw new BadRequestException(ErrorCode.INVALID_APPJAM_SEASON_REQUEST);
        }

        List<SoptampUser> currentGenerationUsers = soptampUserRepository.findAllByGeneration(currentGeneration);
        rankCacheService.deleteAll();
        rankCacheService.addAll(currentGenerationUsers.stream().map(SoptampUserInfo::of).toList());
    }

    @Transactional
    public void deleteAllSoptampUsers() {
        soptampUserRepository.deleteAllInBatch();
        rankCacheService.deleteAll();
    }

    @EventListener(UserWithdrawEvent.class)
    public void handleUserWithdrawEvent(final UserWithdrawEvent event) {
        soptampUserRepository.deleteByUserId(event.getUserId());
    }

    private void raiseScoreCacheSyncEvent(SoptampUser user) {
        if (appjamMode) return;
        if (currentGeneration.equals(user.getGeneration())) {
            eventPublisher.raise(SoptampUserScoreCacheSyncEvent.of(user.getUserId()));
        }
    }

    private void raiseProfileCacheSyncEvent(SoptampUser user) {
        if (appjamMode) return;
        if (currentGeneration.equals(user.getGeneration())) {
            eventPublisher.raise(SoptampUserProfileCacheSyncEvent.of(user.getUserId()));
        }
    }

    private void raiseAllCacheSyncEvent(SoptampUser user) {
        if (appjamMode) return;
        if (currentGeneration.equals(user.getGeneration())) {
            eventPublisher.raise(SoptampUserAllCacheSyncEvent.of(user.getUserId()));
        }
    }
}
