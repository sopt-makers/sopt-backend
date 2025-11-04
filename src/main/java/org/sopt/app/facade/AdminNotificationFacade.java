package org.sopt.app.facade;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.mission.MissionService;
import org.sopt.app.application.platform.PlatformService;
import org.sopt.app.application.platform.dto.PlatformUserInfoResponse;
import org.sopt.app.application.soptamp.SoptampUserFinder;
import org.sopt.app.application.soptamp.SoptampUserInfo;
import org.sopt.app.application.stamp.StampInfo;
import org.sopt.app.application.stamp.StampService;
import org.sopt.app.common.client.notification.AlarmSender;
import org.sopt.app.common.client.notification.dto.request.InstantAlarmRequest;
import org.sopt.app.common.exception.NotFoundException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.common.utils.SoptampDeepLinkBuilder;
import org.sopt.app.domain.entity.soptamp.Mission;
import org.sopt.app.domain.enums.NotificationCategory;
import org.sopt.app.domain.enums.SoptPart;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminNotificationFacade {
    private final AlarmSender<InstantAlarmRequest> instantAlarmSender;
    private final SoptampUserFinder soptampUserFinder;
    private final StampService stampService;
    private final PlatformService platformService;
    private final MissionService missionService;

    /**
     * 현재는 앱의 변경 사항을 적용하기 애매한 부분이 있어서 알림을 두 방향으로 보냄. 1.작성자  2.그 외
     * 추후 클라이언트 측에서 isMine 값 판단 여부를 스탬프 상세 조회에서 하도록 반영되면 deepLink에서 isMine 부분을 제외하고 메서드를 정리해야 함
     */
    public void sendSoptampShowcase(
        Long missionId,
        String nickname,
        String notificationTitle,
        String notificationContent
    ) {
        val owner = soptampUserFinder.findByNickname(nickname);
        val ownerId = owner.getUserId();
        val ownerPart = getOwnerSoptPart(ownerId);
        val mission = missionService.getMissionById(missionId);
        val stamp = stampService.findStamp(missionId, ownerId);

        List<SoptampUserInfo> allOfCurrentGeneration = soptampUserFinder.findAllOfCurrentGeneration();

        Set<String> allWithoutOwner = allOfCurrentGeneration.stream()
            .map(SoptampUserInfo::getUserId)
            .filter(userId -> !Objects.equals(userId, ownerId))
            .map(String::valueOf)
            .collect(Collectors.toSet());

        sendShowcaseWithoutOwner(stamp, nickname, ownerPart, mission, allWithoutOwner, notificationTitle, notificationContent);
        sendShowcaseToOwner(stamp, nickname, ownerPart, mission, Set.of(String.valueOf(ownerId)), notificationTitle, notificationContent);
    }

    private SoptPart getOwnerSoptPart(Long ownerId) {
        val ownerProfile = platformService.getPlatformUserInfoResponse(ownerId);
        val ownerPartName = Optional.ofNullable(ownerProfile.getLatestActivity())
            .map(PlatformUserInfoResponse.SoptActivities::part)
            .orElseThrow(() -> new NotFoundException(ErrorCode.USER_PART_NOT_FOUND));
        return SoptPart.findSoptPartByPartName(ownerPartName);
    }

    private void sendShowcaseWithoutOwner(StampInfo.Stamp stamp, String ownerNickname, SoptPart ownerPart, Mission mission,
        Set<String> allWithoutOwner, String notificationTitle, String notificationContent
    ) {
        val deepLink = SoptampDeepLinkBuilder.buildStampDetailLink(
            stamp.getId(), false, ownerNickname, ownerPart, mission.getId(), mission.getLevel(), mission.getTitle());

        val instantAlarmRequest = InstantAlarmRequest
            .builderWithRequired(allWithoutOwner, notificationTitle, notificationContent, NotificationCategory.NOTICE)
            .deepLink(deepLink)
            .build();

        instantAlarmSender.send(instantAlarmRequest);
    }

    private void sendShowcaseToOwner(StampInfo.Stamp stamp, String ownerNickname, SoptPart ownerPart, Mission mission,
        Set<String> ownerId , String notificationTitle, String notificationContent
    ) {
        val deepLink = SoptampDeepLinkBuilder.buildStampDetailLink(
            stamp.getId(), true, ownerNickname, ownerPart, mission.getId(), mission.getLevel(), mission.getTitle());

        val instantAlarmRequest = InstantAlarmRequest
            .builderWithRequired(ownerId, notificationTitle, notificationContent, NotificationCategory.NOTICE)
            .deepLink(deepLink)
            .build();

        instantAlarmSender.send(instantAlarmRequest);
    }

}
