package org.sopt.app.facade;

import java.util.List;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.sopt.app.application.mission.MissionInfo.Level;
import org.sopt.app.application.mission.MissionService;
import org.sopt.app.application.soptamp.*;
import org.sopt.app.application.stamp.StampInfo.Stamp;
import org.sopt.app.application.stamp.StampService;
import org.sopt.app.domain.entity.soptamp.Mission;
import org.sopt.app.presentation.rank.*;
import org.sopt.app.presentation.stamp.StampRequest;
import org.sopt.app.presentation.stamp.StampRequest.RegisterStampRequest;
import org.sopt.app.presentation.stamp.StampResponse.SoptampReportResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SoptampFacade {

    private final StampService stampService;
    private final MissionService missionService;
    private final SoptampUserService soptampUserService;
    private final RankResponseMapper rankResponseMapper;
    private final SoptampUserFinder soptampUserFinder;

    @Value("${makers.app.soptamp.report.url}")
    private String formUrl;

    @PostConstruct
    public void checkReportUrl() {
        log.info("[CONFIG] 신고 URL 확인 - {}", formUrl);
    }

    @Transactional
    public Stamp uploadStamp(Long userId, RegisterStampRequest registerStampRequest){
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
        log.info("[STAMP DELETE] Request by userId={}, Target stampId={}, Owner userId={}",
            userId, stampId, stamp.getUserId());
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

    public Stamp getStampInfo(Long missionId, String nickname){
        val userId = soptampUserFinder.findByNickname(nickname).getUserId();
        return stampService.findStamp(missionId, userId);
    }

    public RankResponse.Detail findSoptampUserAndCompletedMissionByNickname(String nickname) {
        SoptampUserInfo soptampUserInfo = soptampUserFinder.findByNickname(nickname);
        List<Mission> missionList = missionService.getCompleteMission(soptampUserInfo.getUserId());

        return rankResponseMapper.of(soptampUserInfo, missionList);
    }

    public SoptampReportResponse getReportUrl(){
        return new SoptampReportResponse(formUrl);
    }
}
