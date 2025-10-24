package org.sopt.app.facade;

import java.util.List;

import java.util.Objects;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.sopt.app.application.mission.MissionInfo.Level;
import org.sopt.app.application.mission.MissionService;
import org.sopt.app.application.platform.PlatformService;
import org.sopt.app.application.platform.dto.PlatformUserInfoResponse;
import org.sopt.app.application.platform.PlatformService;
import org.sopt.app.application.soptamp.*;
import org.sopt.app.application.stamp.ClapService;
import org.sopt.app.application.stamp.StampInfo;
import org.sopt.app.application.stamp.StampInfo.Stamp;
import org.sopt.app.application.stamp.StampInfo.StampView;
import org.sopt.app.application.stamp.StampInfo;
import org.sopt.app.application.stamp.StampService;
import org.sopt.app.domain.entity.soptamp.Clap;
import org.sopt.app.domain.entity.soptamp.Mission;
import org.sopt.app.presentation.rank.*;
import org.sopt.app.presentation.stamp.ClapResponse;
import org.sopt.app.presentation.stamp.StampRequest;
import org.sopt.app.presentation.stamp.StampResponse;
import org.sopt.app.presentation.stamp.StampRequest.RegisterStampRequest;
import org.sopt.app.presentation.stamp.StampResponse.SoptampReportResponse;
import org.sopt.app.presentation.stamp.StampResponseMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SoptampFacade {

    private final StampService stampService;
    private final MissionService missionService;
    private final SoptampUserService soptampUserService;
    private final PlatformService platformService;
    private final SoptampUserFinder soptampUserFinder;
    private final ClapService clapService;

    private final RankResponseMapper rankResponseMapper;

    @Value("${makers.app.soptamp.report.url}")
    private String formUrl;

    @Transactional
    public StampInfo.Stamp uploadStamp(Long userId, RegisterStampRequest registerStampRequest){
        stampService.checkDuplicateStamp(userId, registerStampRequest.getMissionId());
        Stamp result = stampService.uploadStamp(registerStampRequest, userId);
        Level mission = missionService.getMissionById(registerStampRequest.getMissionId());
        soptampUserService.addPointByLevel(userId, mission.getLevel());

        return result;
    }

    @Transactional
    public Stamp editStamp(Long userId, StampRequest.EditStampRequest request){
        return stampService.editStampContents(request, userId);
    }

    @Transactional
    public void deleteStamp(Long userId, Long stampId){
        val stamp = stampService.getStampForDelete(stampId, userId);
        val mission = missionService.getMissionById(stamp.getMissionId());
        soptampUserService.subtractPointByLevel(userId, mission.getLevel());

        stampService.deleteStampById(stampId);
    }

    @Transactional
    public void deleteStampAll(Long userId){
        stampService.deleteAllStamps(userId);
        soptampUserService.initPoint(userId);
    }

    @Transactional
    public SoptampUserInfo editSoptampUserProfileMessage(Long userId, String newProfileMessage){
        return soptampUserService.editProfileMessage(userId, newProfileMessage);
    }

    public StampInfo.StampView getStampInfo(Long requestUserId, Long missionId, String nickname){
        val soptampUserId = soptampUserFinder.findByNickname(nickname).getUserId();
        val stamp = stampService.findStamp(missionId, soptampUserId);
        val requestUserClapCount = clapService.getUserClapCount(requestUserId, stamp.getId());

        stampService.increaseViewCountById(stamp.getId());

        return StampInfo.StampView.of(
                stamp, requestUserClapCount, Objects.equals(requestUserId, soptampUserId));
    }

    @Transactional(readOnly = true)
    public ClapResponse.ClapUsersPage getClapUsersPage(Long userId, Long stampId, Pageable pageable) {
        stampService.checkOwnedStamp(stampId, userId);

        val page = clapService.getClapsOfMyStamp(stampId, pageable);
        val userIds = page.getContent().stream()
                .map(Clap::getUserId)
                .distinct()
                .toList();

        val profiles = soptampUserFinder.findUserInfosByIdsAsMap(userIds);
        val platformInfos = platformService.getPlatformUserInfosResponse(userIds);
        val imageMap = platformInfos.stream()
            .collect(Collectors.toMap(
                p -> (long) p.userId(),
                p -> java.util.Optional.ofNullable(p.profileImage()).orElse(""),
                (a, b) -> a,
                java.util.LinkedHashMap::new
            ));

        return new ClapResponse.ClapUsersPage(page, profiles, imageMap);
    }

    public RankResponse.Detail findSoptampUserAndCompletedMissionByNickname(String nickname) {
        SoptampUserInfo soptampUserInfo = soptampUserFinder.findByNickname(nickname);
        List<Mission> missionList = missionService.getCompleteMission(soptampUserInfo.getUserId());

        return rankResponseMapper.of(soptampUserInfo, missionList);
    }

    @Transactional
    public int addClap(Long userId, Long stampId, int increment) {
        return clapService.addClap(userId, stampId, increment);
    }

    @Transactional(readOnly = true)
    public int getStampClapCount(Long stampId) {
        return stampService.getStampClapCount(stampId);
    }

    public SoptampReportResponse getReportUrl(){
        return new SoptampReportResponse(formUrl);
    }
}
