package org.sopt.app.facade;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.auth.PlaygroundAuthService;
import org.sopt.app.application.mission.MissionService;
import org.sopt.app.application.soptamp.SoptampPointService;
import org.sopt.app.application.soptamp.SoptampUserInfo.SoptampUserPlaygroundInfo;
import org.sopt.app.application.soptamp.SoptampUserService;
import org.sopt.app.application.stamp.StampService;
import org.sopt.app.application.user.UserService;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.domain.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminSoptampFacade {

    private final MissionService missionService;
    private final StampService stampService;
    private final SoptampPointService soptampPointService;
    private final SoptampUserService soptampUserService;
    private final PlaygroundAuthService playgroundAuthService;
    private final UserService userService;

    @Transactional
    public void initAllMissionAndStampAndPoints(User user) {
        validateAdminUser(user);
        missionService.deleteAll();
        stampService.deleteAll();
        soptampPointService.deleteAll();
        soptampUserService.initAllSoptampUserPoints();
    }

    @Transactional
    public int initCurrentGenerationInfo(User user) {
        validateAdminUser(user);

        // 플그에서 현재 기수 멤버 아이디 조회
        val currentGenerationPlaygroundIdList = playgroundAuthService.getPlayGroundUserIds(user.getPlaygroundToken())
                .getUserIds();

        // 플그에서 현재 기수 멤버 프로필 조회
        val memberProfileList = playgroundAuthService.getPlaygroundMemberProfiles(
                user.getPlaygroundToken(), currentGenerationPlaygroundIdList);

        // 플그 아이디로 앱 유저 정보 조회
        val userProfileList = userService.getUserProfilesByPlaygroundIds(currentGenerationPlaygroundIdList);

        // 앱 아이디로 솝탬프 유저 정보 조회
        val soptampUserList = soptampUserService.getSoptampUserInfoList(
                userProfileList.stream().map(e -> e.getUserId()).collect(Collectors.toList()));

        // 플그 프로필 리스트와 앱 솝탬프 유저 정보 매핑
        val userInfoList = memberProfileList.stream().map(
                memberProfile -> {
                    val userProfile = userProfileList.stream()
                            .filter(u -> u.getPlaygroundId().equals(memberProfile.getId()))
                            .findFirst();
                    return userProfile.isEmpty() ? null :
                            SoptampUserPlaygroundInfo.builder()
                                    .userId(userProfile.get().getUserId())
                                    .playgroundId(userProfile.get().getPlaygroundId())
                                    .name(userProfile.get().getName())
                                    .generation(Long.parseLong(memberProfile.getLatestActivity().getGeneration()))
                                    .part(memberProfile.getLatestActivity().getPart())
                                    .build();
                }
        ).filter(x -> x != null).collect(Collectors.toList());

        // 플그 파트, 기수, 점수 정보 업데이트
        val updatedSoptampUserList = soptampUserService.initAllCurrentGenerationSoptampUser(soptampUserList,
                userInfoList);

//        // 플그 아이디로 SoptampPoint 현활기수 row 추가
//        val currentGenerationSoptampPointList = soptampPointService.createCurrentGenerationSoptampPointList(
//                updatedSoptampUserList);

        return updatedSoptampUserList.size();
    }

    private void validateAdminUser(User user) {
        // TODO: Admin User 구현 곧 할게요
        if (!user.getUsername().equals("주어랑")) {
            throw new BadRequestException("NONO");
        }
    }
}
