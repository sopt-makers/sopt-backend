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

    /* ==================== 조회/프로필 ==================== */

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

    /* ==================== upsert 진입점 ==================== */

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
        if (appjamMode && profile.getLatestActivity() == null) return;
        if (!appjamMode && profile.getLatestSoptActivity() == null) return;

        Map<Long, SoptampUser> existingUserMap = soptampUserRepository.findByUserId(userId)
            .map(u -> Map.of(userId, u))
            .orElse(Map.of());
        upsertSoptampUserCore(profile, userId, null, existingUserMap, new HashSet<>());
    }

    /**
     * upsert 핵심 로직.
     *
     * @param appjamUserMap   배치: 미리 조회한 AppjamUser 맵 / 단건: null (내부 DB 조회)
     * @param existingUserMap 배치: 미리 조회한 SoptampUser 맵 / 단건: 1건 맵 또는 빈 맵
     * @param reservedNicknames 청크 내 이미 할당된 닉네임 셋 (중복 방지)
     */
    private void upsertSoptampUserCore(PlatformUserInfoResponse profile, Long userId,
            Map<Long, AppjamUser> appjamUserMap,
            Map<Long, SoptampUser> existingUserMap,
            Set<String> reservedNicknames) {
        if (appjamMode) {
            var latest = profile.getLatestActivity();
            if (latest == null) return;
            upsertSoptampUserForAppjam(profile, userId, latest,
                appjamUserMap, existingUserMap, reservedNicknames);
        } else {
            var latestSopt = profile.getLatestSoptActivity();
            if (latestSopt == null) return;
            upsertSoptampUserNormal(profile, userId, latestSopt, existingUserMap, reservedNicknames);
        }
    }

    /* ==================== NORMAL 시즌용 upsert ==================== */

    // 기본 시즌용 upsert (파트 + 이름 기반 닉네임)
    private void upsertSoptampUserNormal(PlatformUserInfoResponse profile, Long userId,
            PlatformUserInfoResponse.SoptActivities latest,
            Map<Long, SoptampUser> existingUserMap,
            Set<String> reservedNicknames) {
        SoptampUser registeredUser = existingUserMap.get(userId);
        if (registeredUser == null) {
            createSoptampUserNormal(profile, userId, latest, reservedNicknames);
            return;
        }
        if (isGenerationChanged(registeredUser, (long) profile.lastGeneration())) {
            updateSoptampUserNormal(registeredUser, profile, latest, reservedNicknames);
        }
    }

    private void updateSoptampUserNormal(SoptampUser registeredUser, PlatformUserInfoResponse profile,
            PlatformUserInfoResponse.SoptActivities latest, Set<String> reservedNicknames) {
        String part = partOrDefault(latest);
        String newNickname = generatePartBasedUniqueNickname(profile.name(), part, registeredUser.getUserId(), reservedNicknames);

        registeredUser.initTotalPoints();
        registeredUser.updateChangedGenerationInfo(
                (long) profile.lastGeneration(),
                findSoptPartByPartName(part),
                newNickname
        );

        raiseAllCacheSyncEvent(registeredUser);
    }

    private void createSoptampUserNormal(PlatformUserInfoResponse profile, Long userId,
            PlatformUserInfoResponse.SoptActivities latest, Set<String> reservedNicknames) {
        String part = partOrDefault(latest);
        String uniqueNickname = generatePartBasedUniqueNickname(profile.name(), part, null, reservedNicknames);
        SoptampUser newSoptampUser = createNewSoptampUser(userId, uniqueNickname, (long) profile.lastGeneration(),
                findSoptPartByPartName(part));
        soptampUserRepository.save(newSoptampUser);
        raiseAllCacheSyncEvent(newSoptampUser);
    }

    private boolean isGenerationChanged(SoptampUser registeredUser, Long profileGeneration) {
        return !registeredUser.getGeneration().equals(profileGeneration);
    }

    // ==================== 앱잼 시즌용 upsert ====================

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

        // 이미 앱잼 규칙이 적용된 닉네임이면 그대로 둠 (비트OOO, 37기OOO 등)
        if (!needsAppjamNicknameMigration(registeredUser, profile.name())) {
            return;
        }

        // 여기까지 오면: 기존 닉네임이 "서버OOO" 같은 파트 기반 → 앱잼 닉네임으로 변환
        String baseNickname = buildAppjamBaseNickname(profile, userId, appjamUserMap);
        String uniqueNickname = generateUniqueNicknameInternal(baseNickname, userId, reservedNicknames);

        // 닉네임이 실제로 바뀌지 않으면 포인트 초기화 없이 종료 (멱등성 보장)
        if (uniqueNickname.equals(registeredUser.getNickname())) {
            return;
        }

        String part = partOrDefault(latest);
        registeredUser.initTotalPoints();
        registeredUser.updateChangedGenerationInfo(
                (long) profile.lastGeneration(),
                findSoptPartByPartName(part),
                uniqueNickname
        );
        raiseAllCacheSyncEvent(registeredUser);
    }

    private void createSoptampUserAppjam(PlatformUserInfoResponse profile,
            Long userId,
            PlatformUserInfoResponse.SoptActivities latest,
            Map<Long, AppjamUser> appjamUserMap,
            Set<String> reservedNicknames) {
        String baseNickname = buildAppjamBaseNickname(profile, userId, appjamUserMap);
        String uniqueNickname = generateUniqueNicknameInternal(baseNickname, null, reservedNicknames);
        String part = partOrDefault(latest);

        SoptampUser newSoptampUser = createNewSoptampUser(
                userId, uniqueNickname, (long) profile.lastGeneration(), findSoptPartByPartName(part));
        soptampUserRepository.save(newSoptampUser);
        raiseAllCacheSyncEvent(newSoptampUser);
    }

    /**
     * "파트명 + 이름" 형식의 구시즌 닉네임이면 앱잼 닉네임으로 변환 필요.
     * profileName을 함께 받아 "파트명"만으로 prefix 체크하는 오탐을 방지.
     * (예: 앱잼 팀명 "서버" + 이름 "김솝트" → "서버김솝트"는 구시즌 형식과 구별 불가 → 변환)
     * (예: 앱잼 팀명 "비트" + 이름 "김솝트" → "비트김솝트"는 어떤 파트 prefix + 이름과도 불일치 → 유지)
     */
    private boolean needsAppjamNicknameMigration(SoptampUser user, String profileName) {
        String nickname = user.getNickname();
        if (nickname == null || nickname.isBlank()) {
            return true;
        }

        for (SoptPart part : SoptPart.values()) {
            if (!part.isSoptPart()) continue;
            if (nickname.startsWith(part.getShortedPartName() + profileName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 앱잼용 base nickname 생성
     * 1. AppjamUser에 있으면: teamName + 이름 (ex. 비트김솝트)
     * 2. 없으면: lastGeneration + "기" + 이름 (ex. 37기김솝트)
     *
     * @param appjamUserMap 배치 호출 시 미리 로드된 맵 (null이면 DB 단건 조회)
     */
    private String buildAppjamBaseNickname(PlatformUserInfoResponse profile, Long userId,
            Map<Long, AppjamUser> appjamUserMap) {
        return findAppjamUser(userId, appjamUserMap)
            .map(u -> u.getTeamName() + profile.name())
            .orElseGet(() -> profile.lastGeneration() + "기" + profile.name());
    }

    private Optional<AppjamUser> findAppjamUser(Long userId, Map<Long, AppjamUser> appjamUserMap) {
        return (appjamUserMap != null)
            ? Optional.ofNullable(appjamUserMap.get(userId))
            : appjamUserRepository.findByUserId(userId);
    }

    // ==================== 닉네임 유니크 로직 공통부 ====================

    private static final String SUFFIX_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private static String partOrDefault(PlatformUserInfoResponse.SoptActivities activity) {
        return activity == null || activity.part() == null ? "미상" : activity.part();
    }

    /**
     * 파트 기반 닉네임 (NORMAL 시즌용)
     * ex. "서버" + "김솝트" → "서버김솝트"
     */
    private String generatePartBasedUniqueNickname(String name, String part, Long currentUserIdOrNull,
            Set<String> reservedNicknames) {
        String baseNickname = SoptPart.findSoptPartByPartName(part).getShortedPartName() + name;
        return generateUniqueNicknameInternal(baseNickname, currentUserIdOrNull, reservedNicknames);
    }

    /**
     * baseNickname을 기준으로 전역 유니크 닉네임 생성.
     * - currentUserIdOrNull == null : 새 유저 생성 (existsByNickname)
     * - currentUserIdOrNull != null : 내 row 제외하고 중복 체크
     * - reservedNicknames : 같은 청크 내에서 이미 할당된 닉네임 (DB 조회 없이 충돌 방지)
     */
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

    // ==================== 포인트/회원 탈퇴 로직 ====================

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
