package org.sopt.app.application.soptamp;

import static org.sopt.app.domain.entity.soptamp.SoptampUser.createNewSoptampUser;
import static org.sopt.app.domain.enums.SoptPart.findSoptPartByPartName;

import java.util.*;

import org.sopt.app.application.platform.dto.PlatformUserInfoResponse;
import org.sopt.app.application.rank.CachedUserInfo;
import org.sopt.app.application.rank.RankCacheService;
import org.sopt.app.application.user.UserWithdrawEvent;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.soptamp.SoptampUser;
import org.sopt.app.domain.enums.SoptPart;
import org.sopt.app.interfaces.postgres.AppjamUserRepository;
import org.sopt.app.interfaces.postgres.SoptampUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SoptampUserService {

    private final SoptampUserRepository soptampUserRepository;
    private final AppjamUserRepository appjamUserRepository;
    private final RankCacheService rankCacheService;

    @Value("${makers.app.soptamp.appjam-mode:false}")
    private boolean appjamMode;

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
        rankCacheService.updateCachedUserInfo(soptampUser.getUserId(), CachedUserInfo.of(SoptampUserInfo.of(soptampUser)));
        return SoptampUserInfo.of(soptampUser);
    }

    /* ==================== upsert 진입점 ==================== */

    // 앱잼 시즌 여부에 따라 upsert 로직 분기
    @Transactional
    public void upsertSoptampUser(PlatformUserInfoResponse profile, Long userId) {
        if (profile == null)
            return;
        var latest = profile.getLatestActivity();
        if (latest == null)
            return;

        if (appjamMode) {
            upsertSoptampUserForAppjam(profile, userId, latest);
        } else {
            upsertSoptampUserNormal(profile, userId, latest);
        }
    }

    /* ==================== NORMAL 시즌용 upsert ==================== */

    // 기본 시즌용 upsert (파트 + 이름 기반 닉네임)
    private void upsertSoptampUserNormal(PlatformUserInfoResponse profile, Long userId,
        PlatformUserInfoResponse.SoptActivities latest) {
        Optional<SoptampUser> user = soptampUserRepository.findByUserId(userId);
        if (user.isEmpty()) {
            this.createSoptampUserNormal(profile, userId, latest);
            return;
        }
        SoptampUser registeredUser = user.get();
        if(this.isGenerationChanged(registeredUser, (long)profile.lastGeneration())) {
            updateSoptampUserNormal(registeredUser, profile, latest);
        }
    }

    private void updateSoptampUserNormal(SoptampUser registeredUser, PlatformUserInfoResponse profile, PlatformUserInfoResponse.SoptActivities latest){
        Long userId = registeredUser.getUserId();
        String part = latest.part() == null ? "미상" : latest.part();
        String newNickname = generatePartBasedUniqueNickname(profile.name(), part, userId);

        registeredUser.initTotalPoints();
        registeredUser.updateChangedGenerationInfo(
            (long)profile.lastGeneration(),
            findSoptPartByPartName(part),
            newNickname
        );
        rankCacheService.removeRank(userId);
        rankCacheService.createNewRank(userId);
    }

    private void createSoptampUserNormal(PlatformUserInfoResponse profile, Long userId, PlatformUserInfoResponse.SoptActivities latest) {
        String part = latest.part() == null ? "미상" : latest.part();
        String uniqueNickname = generatePartBasedUniqueNickname(profile.name(), part, null);
        SoptampUser newSoptampUser = createNewSoptampUser(userId, uniqueNickname, (long)profile.lastGeneration(), findSoptPartByPartName(part));
        soptampUserRepository.save(newSoptampUser);
        rankCacheService.createNewRank(userId);
    }

    private boolean isGenerationChanged(SoptampUser registeredUser, Long profileGeneration) {
        return !registeredUser.getGeneration().equals(profileGeneration);
    }

    // ==================== 앱잼 시즌용 upsert ====================

    private void upsertSoptampUserForAppjam(PlatformUserInfoResponse profile,
        Long userId,
        PlatformUserInfoResponse.SoptActivities latest) {
        Optional<SoptampUser> userOpt = soptampUserRepository.findByUserId(userId);

        if (userOpt.isEmpty()) {
            createSoptampUserAppjam(profile, userId, latest);
            return;
        }

        SoptampUser registeredUser = userOpt.get();

        // 이미 앱잼 규칙이 적용된 닉네임이면 그대로 둠 (비트OOO, 37기OOO 등)
        if (!needsAppjamNicknameMigration(registeredUser)) {
            return;
        }

        // 여기까지 오면: 기존 닉네임이 "서버OOO" 같은 파트 기반 → 앱잼 닉네임으로 변환
        String baseNickname = buildAppjamBaseNickname(profile, userId);

        String uniqueNickname = generateUniqueNicknameInternal(baseNickname, userId);

        String part = latest.part() == null ? "미상" : latest.part();

        registeredUser.updateChangedGenerationInfo(
            (long) profile.lastGeneration(),
            findSoptPartByPartName(part),
            uniqueNickname
        );

        // 앱잼 변환 시점에 한 번 포인트 초기화
        registeredUser.initTotalPoints();
    }

    private void createSoptampUserAppjam(PlatformUserInfoResponse profile,
        Long userId,
        PlatformUserInfoResponse.SoptActivities latest) {

        String baseNickname = buildAppjamBaseNickname(profile, userId);

        // 새 유저: 전체에서 중복 검사
        String uniqueNickname = generateUniqueNicknameInternal(
            baseNickname,
            null
        );

        String part = latest.part() == null ? "미상" : latest.part();

        SoptampUser newSoptampUser = createNewSoptampUser(
            userId,
            uniqueNickname,
            (long) profile.lastGeneration(),
            findSoptPartByPartName(part)
        );
        newSoptampUser.initTotalPoints(); // 새 시즌이니 0점부터

        soptampUserRepository.save(newSoptampUser);
    }

    private boolean needsAppjamNicknameMigration(SoptampUser user) {
        String nickname = user.getNickname();
        if (nickname == null || nickname.isBlank()) {
            // 닉네임이 비어 있으면 앱잼 규칙으로 한 번 세팅해 주는 게 자연스러움
            return true;
        }

        // SoptPart 기준으로 "서버", "기획" 같은 축약/프리픽스를 모두 검사
        for (SoptPart part : SoptPart.values()) {
            String prefix = part.getShortedPartName();
            if (nickname.startsWith(prefix)) {
                // 서버김솝트, 디자인김솝트 등 → 기존 시즌(파트 기반) 닉네임이므로 앱잼 변환 필요
                return true;
            }
        }

        // 그 외 (비트김솝트, 37기김솝트 등) → 이미 앱잼 스타일로 적용된 걸로 간주
        return false;
    }

    /**
     * 앱잼용 base nickname 생성
     * 1. AppjamUser에 있으면: teamName + 이름 (ex. 비트김솝트)
     * 2. 없으면: lastGeneration + "기" + 이름 (ex. 37기김솝트)
     */
    private String buildAppjamBaseNickname(PlatformUserInfoResponse profile, Long userId) {
        return appjamUserRepository.findByUserId(userId)
            .map(appjamUser -> appjamUser.getTeamName() + profile.name())
            .orElseGet(() -> profile.lastGeneration() + "기" + profile.name());
    }

    // ==================== 닉네임 유니크 로직 공통부 ====================

    /**
     * 파트 기반 닉네임 (NORMAL 시즌용)
     * ex. "서버" + "김솝트" → "서버김솝트"
     */
    private String generatePartBasedUniqueNickname(String name, String part, Long currentUserIdOrNull) {
        String prefixPartName = SoptPart.findSoptPartByPartName(part).getShortedPartName();
        String baseNickname = prefixPartName + name;
        return generateUniqueNicknameInternal(baseNickname, currentUserIdOrNull);
    }

    /**
     * baseNickname을 기준으로, 전역 유니크 닉네임 생성
     * - currentUserIdOrNull == null  : 새 유저 생성 (그냥 existsByNickname)
     * - currentUserIdOrNull != null : 내 row는 제외하고 중복 체크
     */
    private String generateUniqueNicknameInternal(String baseNickname, Long currentUserIdOrNull) {
        if (!existsNickname(baseNickname, currentUserIdOrNull)) {
            return baseNickname;
        }

        char suffix = 'A';
        for (int i = 0; i < 52; i++, suffix++) {
            String candidate = baseNickname + suffix;
            if (!existsNickname(candidate, currentUserIdOrNull)) {
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

        if (!appjamMode) {
            rankCacheService.incrementScore(soptampUser.getUserId(), level);
        }
    }

    @Transactional
    public void subtractPointByLevel(Long userId, Integer level) {
        SoptampUser soptampUser = soptampUserRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        soptampUser.subtractPointsByLevel(level);

        if (!appjamMode) {
            rankCacheService.decreaseScore(soptampUser.getUserId(), level);
        }
    }

    @Transactional
    public void initPoint(Long userId) {
        SoptampUser soptampUser = soptampUserRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        soptampUser.initTotalPoints();
        soptampUserRepository.save(soptampUser);
        if (!appjamMode) {
            rankCacheService.initScore(soptampUser.getUserId());
        }
    }

    @Transactional
    public void initAllSoptampUserPoints() {
        List<SoptampUser> soptampUserList = soptampUserRepository.findAll();
        soptampUserList.forEach(SoptampUser::initTotalPoints);
        soptampUserRepository.saveAll(soptampUserList);
        if (!appjamMode) {
            rankCacheService.deleteAll();
            rankCacheService.addAll(soptampUserList.stream().map(SoptampUserInfo::of).toList());
        }
    }

    @Transactional
    public void initSoptampRankCache() {
        if (appjamMode) {
            throw new BadRequestException(ErrorCode.INVALID_APPJAM_SEASON_REQUEST);
        }

        List<SoptampUser> soptampUserList = soptampUserRepository.findAll();
        rankCacheService.deleteAll();
        rankCacheService.addAll(soptampUserList.stream().map(SoptampUserInfo::of).toList());
    }

    @EventListener(UserWithdrawEvent.class)
    public void handleUserWithdrawEvent(final UserWithdrawEvent event) {
        soptampUserRepository.deleteByUserId(event.getUserId());
    }
}