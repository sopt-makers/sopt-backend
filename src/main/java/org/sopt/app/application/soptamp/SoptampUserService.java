package org.sopt.app.application.soptamp;

import static org.sopt.app.domain.entity.soptamp.SoptampUser.createNewSoptampUser;
import static org.sopt.app.domain.enums.PlaygroundPart.findPlaygroundPartByPartName;

import java.util.*;
import lombok.*;

import org.sopt.app.application.platform.dto.PlatformUserInfoResponse;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.ActivityCardinalInfo;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.PlaygroundProfile;
import org.sopt.app.application.rank.CachedUserInfo;
import org.sopt.app.application.rank.RankCacheService;
import org.sopt.app.application.user.UserWithdrawEvent;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.soptamp.SoptampUser;
import org.sopt.app.domain.enums.PlaygroundPart;
import org.sopt.app.interfaces.postgres.SoptampUserRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SoptampUserService {

    private final SoptampUserRepository soptampUserRepository;
    private final RankCacheService rankCacheService;

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

    @Transactional
    public void upsertSoptampUser(PlatformUserInfoResponse profile, Long userId) {
        if (profile == null) return;
        var latest = profile.getLatestActivity();
        if (latest == null) return;

        Optional<SoptampUser> user = soptampUserRepository.findByUserId(userId);
        if (user.isEmpty()) {
            this.createSoptampUser(profile, userId, latest);
            return;
        }
        SoptampUser registeredUser = user.get();
        if(this.isGenerationChanged(registeredUser, (long)profile.lastGeneration())) {
            updateSoptampUser(registeredUser, profile, latest);
        }
    }

    private void updateSoptampUser(SoptampUser registeredUser, PlatformUserInfoResponse profile, PlatformUserInfoResponse.SoptActivities latest){
        Long userId = registeredUser.getUserId();
        String part = latest.part() == null ? "미상" : latest.part();
        String newNickname = generateUniqueNickname(profile.name(), part);
        registeredUser.initTotalPoints();
        registeredUser.updateChangedGenerationInfo(
            (long)profile.lastGeneration(),
            findPlaygroundPartByPartName(part),
            newNickname
        );
        rankCacheService.removeRank(userId);
        rankCacheService.createNewRank(userId);
    }

    private void createSoptampUser(PlatformUserInfoResponse profile, Long userId, PlatformUserInfoResponse.SoptActivities latest) {
        String part = latest.part() == null ? "미상" : latest.part();
        String uniqueNickname = generateUniqueNickname(profile.name(), part);
        SoptampUser newSoptampUser = createNewSoptampUser(userId, uniqueNickname, (long)profile.lastGeneration(), findPlaygroundPartByPartName(part));
        soptampUserRepository.save(newSoptampUser);
        rankCacheService.createNewRank(userId);
    }

    private boolean isGenerationChanged(SoptampUser registeredUser, Long profileGeneration) {
        return !registeredUser.getGeneration().equals(profileGeneration);
    }

    private String generateUniqueNickname(String nickname, String part) {
        String prefixPartName = PlaygroundPart.findPlaygroundPartByPartName(part).getShortedPartName();
        StringBuilder uniqueNickname = new StringBuilder().append(prefixPartName).append(nickname);
        if (soptampUserRepository.existsByNickname(uniqueNickname.toString())) {
            return addSuffixToNickname(uniqueNickname);
        }
        return uniqueNickname.toString();
    }

    private String addSuffixToNickname(StringBuilder uniqueNickname) {
        char suffix = 'A';
        uniqueNickname.append(suffix);
        for(int i = 0; i < 52; i++) {
            if (!soptampUserRepository.existsByNickname(uniqueNickname.toString())) {
                return uniqueNickname.toString();
            }
            uniqueNickname.deleteCharAt(uniqueNickname.length() - 1);
            uniqueNickname.append(++suffix);
        }
        throw new BadRequestException(ErrorCode.NICKNAME_IS_FULL);
    }

    @Transactional
    public void addPointByLevel(Long userId, Integer level) {
        SoptampUser soptampUser = soptampUserRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        soptampUser.addPointsByLevel(level);
        rankCacheService.incrementScore(soptampUser.getUserId(), level);
    }

    @Transactional
    public void subtractPointByLevel(Long userId, Integer level) {
        SoptampUser soptampUser = soptampUserRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        soptampUser.subtractPointsByLevel(level);
        rankCacheService.decreaseScore(soptampUser.getUserId(), level);
    }

    @Transactional
    public void initPoint(Long userId) {
        SoptampUser soptampUser = soptampUserRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        soptampUser.initTotalPoints();
        soptampUserRepository.save(soptampUser);
        rankCacheService.initScore(soptampUser.getUserId());
    }

    @Transactional
    public void initAllSoptampUserPoints() {
        val soptampUserList = soptampUserRepository.findAll();
        soptampUserList.forEach(SoptampUser::initTotalPoints);
        soptampUserRepository.saveAll(soptampUserList);
        rankCacheService.deleteAll();
        rankCacheService.addAll(soptampUserList.stream().map(SoptampUserInfo::of).toList());
    }

    @EventListener(UserWithdrawEvent.class)
    public void handleUserWithdrawEvent(final UserWithdrawEvent event) {
        soptampUserRepository.deleteByUserId(event.getUserId());
    }
}