package org.sopt.app.facade;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;

import org.sopt.app.application.appjamuser.AppjamUserService;
import org.sopt.app.application.fortune.FortuneService;
import org.sopt.app.application.friend.FriendService;
import org.sopt.app.application.platform.PlatformService;
import org.sopt.app.application.platform.dto.PlatformUserInfoResponse;
import org.sopt.app.application.playground.PlaygroundAuthService;
import org.sopt.app.application.notification.NotificationService;
import org.sopt.app.application.appservice.AppServiceService;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo;
import org.sopt.app.application.poke.PokeService;
import org.sopt.app.application.stamp.ClapService;
import org.sopt.app.application.stamp.StampService;
import org.sopt.app.application.user.UserInfo;
import org.sopt.app.application.user.UserService;
import org.sopt.app.domain.enums.Friendship;
import org.sopt.app.domain.enums.UserStatus;
import org.sopt.app.presentation.user.UserResponse;
import org.sopt.app.presentation.user.UserResponse.*;
import org.sopt.app.presentation.user.UserResponseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final PlaygroundAuthService playgroundAuthService;
    private final NotificationService notificationService;
    private final AppServiceService appServiceService;
    private final UserResponseMapper userResponseMapper;
    private final PlatformService platformService;
    private final StampService stampService;
    private final ClapService clapService;
    private final FriendService friendService;
    private final PokeService pokeService;
    private final FortuneService fortuneService;
    private final UserService userService;
    private final AppjamUserService appjamUserService;

    @Transactional(readOnly = true)
    public MainView getMainViewInfo(Long userId) {
        if(userId == null) {
            return MainView.unauthenticatedMainView();
        }
        PlatformUserInfoResponse platformUserInfoResponse = platformService.getPlatformUserInfoResponse(userId);
        PlaygroundProfileInfo.MainViewUser mainViewUser = PlaygroundProfileInfo.MainViewUser.builder()
            .name(platformUserInfoResponse.name())
            .status(platformService.getStatus(userId))
            .profileImage(platformUserInfoResponse.profileImage())
            .generationList(platformService.getMemberGenerationList(userId).stream().toList())
            .build();

        boolean mainViewNotification = notificationService.getNotificationConfirmStatus(userId);
        return userResponseMapper.ofMainView(PlaygroundProfileInfo.MainView.of(mainViewUser), Operation.defaultOperation(), mainViewNotification);
    }

    @Transactional(readOnly = true)
    @Deprecated
    public List<AppService> getAppServiceInfo() {
        return appServiceService.getAllAppService().stream()
                .map(AppService::of)
                .toList();
    }

    @Transactional
    public UserInfo createUser(Long requestUserId){
        return userService.createUser(requestUserId);
    }

    @Transactional
    public void deleteUser(Long userId) {
        userService.deleteUser(userId);
    }

    @Transactional(readOnly = true)
    public UserResponse.MySoptLog getMySoptLog(Long userId) {
        UserStatus userStatus = platformService.getStatus(userId);
        boolean isActive = (userStatus == UserStatus.ACTIVE);

        boolean isAppjamParticipant = appjamUserService.isAppjamParticipant(userId);

        boolean isFortuneChecked = fortuneService.isExistTodayFortune(userId);
        String todayFortuneText = isFortuneChecked
            ? fortuneService.getTodayFortuneWordByUserId(userId, LocalDate.now()).title()
            : "오늘 내 운세는?";

        int totalPokeCount = pokeService.getUserPokeCount(userId).intValue();
        int newFriendsPokeCount = friendService.sumPokeCountByFriendship(
            userId, Friendship.NEW_FRIEND.getLowerLimit(), Friendship.NEW_FRIEND.getUpperLimit());
        int bestFriendsPokeCount = friendService.sumPokeCountByFriendship(
            userId, Friendship.BEST_FRIEND.getLowerLimit(), Friendship.BEST_FRIEND.getUpperLimit());
        int soulmatesPokeCount = friendService.sumPokeCountByFriendship(
            userId, Friendship.SOULMATE.getLowerLimit(), Friendship.SOULMATE.getUpperLimit());

        boolean isSoptampIncluded = isActive || isAppjamParticipant;

        if (isSoptampIncluded) {
            int soptampCount = stampService.getCompletedMissionCount(userId);
            int viewCount = stampService.getTotalViewCount(userId);
            int myClapCount = stampService.getTotalReceivedClapCount(userId);
            int clapCount = clapService.getTotalGivenClapCount(userId);

            if (isActive) {
                return UserResponse.MySoptLog.ofActive(
                    isAppjamParticipant,
                    isFortuneChecked,
                    todayFortuneText,
                    soptampCount,
                    viewCount,
                    myClapCount,
                    clapCount,
                    totalPokeCount,
                    newFriendsPokeCount,
                    bestFriendsPokeCount,
                    soulmatesPokeCount
                );
            }

            return UserResponse.MySoptLog.ofInactiveAppjamParticipant(
                isFortuneChecked,
                todayFortuneText,
                soptampCount,
                viewCount,
                myClapCount,
                clapCount,
                totalPokeCount,
                newFriendsPokeCount,
                bestFriendsPokeCount,
                soulmatesPokeCount
            );
        }

        return UserResponse.MySoptLog.ofInactiveNonAppjam(
            isFortuneChecked,
            todayFortuneText,
            totalPokeCount,
            newFriendsPokeCount,
            bestFriendsPokeCount,
            soulmatesPokeCount
        );
    }
}
