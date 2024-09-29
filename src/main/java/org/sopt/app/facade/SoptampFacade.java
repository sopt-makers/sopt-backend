package org.sopt.app.facade;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.mission.MissionInfo.Level;
import org.sopt.app.application.mission.MissionService;
import org.sopt.app.application.s3.S3Service;
import org.sopt.app.application.soptamp.SoptampUserFinder;
import org.sopt.app.application.soptamp.SoptampUserInfo;
import org.sopt.app.application.soptamp.SoptampUserService;
import org.sopt.app.application.stamp.StampInfo;
import org.sopt.app.application.stamp.StampInfo.Stamp;
import org.sopt.app.application.stamp.StampService;
import org.sopt.app.domain.entity.soptamp.Mission;
import org.sopt.app.presentation.rank.RankResponse;
import org.sopt.app.presentation.rank.RankResponseMapper;
import org.sopt.app.presentation.stamp.StampRequest;
import org.sopt.app.presentation.stamp.StampRequest.RegisterStampRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SoptampFacade {

    private final StampService stampService;
    private final S3Service s3Service;
    private final MissionService missionService;
    private final SoptampUserService soptampUserService;
    private final RankResponseMapper rankResponseMapper;
    private final SoptampUserFinder soptampUserFinder;

    @Transactional
    public StampInfo.Stamp uploadStampDeprecated(Long userId, Long missionId, RegisterStampRequest registerStampRequest, List<MultipartFile> multipartFileList){
        stampService.checkDuplicateStamp(userId, missionId);
        val imgPaths = s3Service.uploadDeprecated(multipartFileList);
        val result = stampService.uploadStampDeprecated(registerStampRequest, imgPaths, userId, missionId);
        val mission = missionService.getMissionById(missionId);
        soptampUserService.addPointByLevel(userId, mission.getLevel());
        return result;
    }

    @Transactional
    public StampInfo.Stamp uploadStamp(Long userId, RegisterStampRequest registerStampRequest){
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

    @Transactional
    public StampInfo.Stamp getStampInfo(Long missionId, String nickname){
        val userId = soptampUserFinder.findByNickname(nickname).getUserId();
        return stampService.findStamp(missionId, userId);
    }

    @Transactional
    public StampInfo.Stamp editStamp(StampRequest.EditStampRequest editStampRequest, Long userId, Long missionId, List<MultipartFile> multipartFiles){
        val stamp = stampService.editStampContentsDeprecated(editStampRequest, userId, missionId);
        val imgPaths = s3Service.uploadDeprecated(multipartFiles);
        if (!imgPaths.isEmpty()) {
            stampService.editStampImagesDeprecated(stamp, imgPaths);
        }
        return stamp;
    }

    public RankResponse.Detail findSoptampUserAndCompletedMissionByNickname(String nickname) {
        SoptampUserInfo soptampUserInfo = soptampUserFinder.findSoptampUserByNickname(nickname);
        List<Mission> missionList = missionService.getCompleteMission(soptampUserInfo.getUserId());

        return rankResponseMapper.of(soptampUserInfo, missionList);
    }
}
