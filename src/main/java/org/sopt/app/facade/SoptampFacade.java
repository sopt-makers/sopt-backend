package org.sopt.app.facade;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.mission.MissionInfo.Level;
import org.sopt.app.application.mission.MissionService;
import org.sopt.app.application.soptamp.*;
import org.sopt.app.application.stamp.StampInfo.Stamp;
import org.sopt.app.application.stamp.StampService;
import org.sopt.app.domain.entity.soptamp.Mission;
import org.sopt.app.presentation.rank.*;
import org.sopt.app.presentation.stamp.StampRequest.RegisterStampRequest;
import org.sopt.app.presentation.stamp.StampResponse.SoptampReportResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public Stamp uploadStamp(Long userId, RegisterStampRequest registerStampRequest){
        stampService.checkDuplicateStamp(userId, registerStampRequest.getMissionId());
        Stamp result = stampService.uploadStamp(registerStampRequest, userId);
        Level mission = missionService.getMissionById(registerStampRequest.getMissionId());
        soptampUserService.addPointByLevel(userId, mission.getLevel());
        return result;
    }

    @Transactional
    public void deleteStamp(Long userId, Long stampId){
        val missionId = stampService.getMissionIdByStampId(stampId);
        val mission = missionService.getMissionById(missionId);
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
