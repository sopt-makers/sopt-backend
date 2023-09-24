package org.sopt.app.application.stamp;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.mission.MissionService;
import org.sopt.app.application.s3.S3Service;
import org.sopt.app.application.soptamp.SoptampPointService;
import org.sopt.app.application.soptamp.SoptampUserService;
import org.sopt.app.domain.entity.Stamp;
import org.sopt.app.presentation.stamp.StampRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class StampFacade {

    private final StampService stampService;
    private final S3Service s3Service;
    private final MissionService missionService;
    private final SoptampUserService soptampUserService;
    private final SoptampPointService soptampPointService;

    @Transactional
    public Stamp uploadStampDeprecated(Long userId, Long missionId, StampRequest.RegisterStampRequest registerStampRequest, List<MultipartFile> multipartFileList){
        stampService.checkDuplicateStamp(userId, missionId);
        val imgPaths = s3Service.uploadDeprecated(multipartFileList);
        val result = stampService.uploadStampDeprecated(registerStampRequest, imgPaths, userId, missionId);
        val mission = missionService.getMissionById(missionId);
        val soptampUser = soptampUserService.addPoint(userId, mission.getLevel());
        soptampPointService.addPoint(soptampUser.getId(), mission.getLevel());
        return result;
    }

    @Transactional
    public Stamp uploadStamp(Long userId, StampRequest.RegisterStampRequest registerStampRequest){
        stampService.checkDuplicateStamp(userId, registerStampRequest.getMissionId());
        val result = stampService.uploadStamp(registerStampRequest, userId);
        val mission = missionService.getMissionById(registerStampRequest.getMissionId());
        val soptampUser = soptampUserService.addPoint(userId, mission.getLevel());
        soptampPointService.addPoint(soptampUser.getId(), mission.getLevel());
        return result;
    }

    @Transactional
    public void deleteStamp(Long userId, Long stampId){
        val missionId = stampService.getMissionIdByStampId(stampId);
        val mission = missionService.getMissionById(missionId);
        val soptampUser = soptampUserService.subtractPoint(userId, mission.getLevel());
        soptampPointService.subtractPoint(soptampUser.getId(), mission.getLevel());
        stampService.deleteStampById(stampId);
    }

    @Transactional
    public void deleteStampAll(Long userId){
        stampService.deleteAllStamps(userId);
        soptampUserService.initPoint(userId);
    }
}
