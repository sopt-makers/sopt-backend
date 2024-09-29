package org.sopt.app.facade;

import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.playground.PlaygroundAuthService;
import org.sopt.app.application.mission.MissionService;
import org.sopt.app.application.soptamp.SoptampUserPlaygroundInfo;
import org.sopt.app.application.soptamp.SoptampUserService;
import org.sopt.app.application.stamp.StampService;
import org.sopt.app.application.user.UserInfo.UserProfile;
import org.sopt.app.application.user.UserService;
import org.sopt.app.domain.entity.User;
import org.sopt.app.presentation.admin.AdminSoptampResponse;
import org.sopt.app.presentation.admin.AdminSoptampResponse.Rows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminSoptampFacade {

    private final MissionService missionService;
    private final StampService stampService;
    private final SoptampUserService soptampUserService;
    private final PlaygroundAuthService playgroundAuthService;
    private final UserService userService;

    @Transactional
    public void initAllMissionAndStampAndPoints() {
        missionService.deleteAll();
        stampService.deleteAll();
        soptampUserService.initAllSoptampUserPoints();
    }

    @Transactional
    public AdminSoptampResponse.Rows initCurrentGenerationInfo(User user) {
        // 플그에서 현재 기수 멤버 아이디 조회
        val currentGenerationPlaygroundIdList =
                playgroundAuthService.getPlayGroundUserIds(user.getPlaygroundToken()).userIds();

        // 플그에서 현재 기수 멤버 프로필 조회
        val memberProfileList = playgroundAuthService.getPlaygroundMemberProfiles(
                user.getPlaygroundToken(), currentGenerationPlaygroundIdList);

        // 플그 아이디로 앱 유저 정보 조회
        val userProfileList = userService.getUserProfilesByPlaygroundIds(currentGenerationPlaygroundIdList);

        // 앱 아이디로 솝탬프 유저 정보 조회
        val soptampUserList = soptampUserService.getSoptampUserInfoList(
                userProfileList.stream().map(UserProfile::getUserId).collect(Collectors.toList()));

        // 플그 프로필 리스트와 앱 솝탬프 유저 정보 매핑
        val userInfoList = memberProfileList.stream().map(
                memberProfile -> {
                    val userProfile = userProfileList.stream()
                            .filter(u -> u.getPlaygroundId().equals(memberProfile.getMemberId()))
                            .findFirst();
                    return userProfile.map(profile -> SoptampUserPlaygroundInfo.builder()
                            .userId(profile.getUserId())
                            .playgroundId(profile.getPlaygroundId())
                            .name(profile.getName())
                            .generation(memberProfile.getLatestActivity().getGeneration())
                            .part(memberProfile.getLatestActivity().getPart())
                            .build()).orElse(null);
                }
        ).filter(Objects::nonNull).toList();

        // 플그 파트, 기수, 점수 정보 업데이트
        val updatedSoptampUserList =
                soptampUserService.initAllCurrentGenerationSoptampUser(soptampUserList, userInfoList);

        return Rows.builder()
                .soptampUserRows(updatedSoptampUserList.size())
                .build();
    }
}
