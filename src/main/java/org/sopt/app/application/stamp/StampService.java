package org.sopt.app.application.stamp;

import static org.sopt.app.common.ResponseCode.INVALID_RESPONSE;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.common.exception.ApiException;
import org.sopt.app.domain.entity.Stamp;
import org.sopt.app.interfaces.postgres.MissionRepository;
import org.sopt.app.interfaces.postgres.StampRepository;
import org.sopt.app.interfaces.postgres.UserRepository;
import org.sopt.app.presentation.stamp.StampRequest;
import org.sopt.app.presentation.stamp.StampRequest.RegisterStampRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class StampService {

    private final StampRepository stampRepository;

    private final UserRepository userRepository;

    private final MissionRepository missionRepository;

    @Transactional(readOnly = true)
    public Stamp findStamp(String userId, Long missionId) {
        return stampRepository.findByUserIdAndMissionId(Long.valueOf(userId), missionId);
    }

    @Transactional
    public Stamp uploadStamp(
            RegisterStampRequest stampRequest,
            List<String> imgPaths,
            String userId,
            Long missionId) {
        val imgList = new ArrayList<String>(imgPaths);
        val stamp = this.convertStampImg(stampRequest, imgList, userId, missionId);

        //랭크 관련 점수 처리
        val user = userRepository.findUserById(Long.valueOf(userId))
                .orElseThrow(() -> new ApiException(INVALID_RESPONSE));

        //미션 랭크점수 알아오기
        val mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new ApiException(INVALID_RESPONSE));

        user.addPoints(mission.getLevel());
        userRepository.save(user);

        return stampRepository.save(stamp);
    }

    //사진 수정 할 경우
    @Transactional
    public Stamp editStampWithImg(
            StampRequest.EditStampRequest editStampRequest,
            List<String> imgPaths,
            String userId,
            Long missionId) {

        val stamp = stampRepository.findByUserIdAndMissionId(Long.valueOf(userId), missionId);

        if (StringUtils.hasText(editStampRequest.getContents())) {
            stamp.changeContents(editStampRequest.getContents());
        }
        stamp.changeImages(imgPaths);
        stamp.setUpdatedAt(LocalDateTime.now());

        return stampRepository.save(stamp);
    }


    //사진 수정 안할 경우
    @Transactional
    public Stamp editStampContents(
            StampRequest.EditStampRequest editStampRequest,
            String userId,
            Long missionId) {

        val stamp = stampRepository.findByUserIdAndMissionId(Long.valueOf(userId), missionId);
        if (StringUtils.hasText(editStampRequest.getContents())) {
            stamp.changeContents(editStampRequest.getContents());
        }

        stamp.setUpdatedAt(LocalDateTime.now());
        return stampRepository.save(stamp);
    }


    //Stamp 삭제 by stampId
    @Transactional
    public void deleteByStampId(Long stampId) {

        val stamp = stampRepository.findById(stampId)
                .orElseThrow(() -> new ApiException(INVALID_RESPONSE));

        //랭크 관련 점수 처리
        val user = userRepository.findUserById(stamp.getUserId())
                .orElseThrow(() -> new ApiException(INVALID_RESPONSE));

        //미션 랭크점수 알아오기
        val mission = missionRepository.findById(stamp.getMissionId())
                .orElseThrow(() -> new ApiException(INVALID_RESPONSE));

        user.minusPoints(mission.getLevel());
        userRepository.save(user);

        stampRepository.deleteById(stampId);
    }


    //스탬프 중복 검사체크
    @Transactional(readOnly = true)
    public boolean checkDuplicateStamp(String userId, Long missionId) {
        val stamp = stampRepository.findByUserIdAndMissionId(Long.valueOf(userId), missionId);
        return stamp != null;
    }


    //Stamp 삭제 All by UserId
    @Transactional
    public void deleteStampByUserId(Long userId) {

        //스탬프 전부삭제
        stampRepository.deleteAllByUserId(userId);

        //해당 스탬프로 얻었던 점수 모두 초기화
        val user = userRepository.findUserById(userId)
                .orElseThrow(() -> new ApiException(INVALID_RESPONSE));
        user.initializePoints();
        userRepository.save(user);

    }


    //Stamp Entity 양식에 맞게 데이터 세팅
    private Stamp convertStampImg(
            RegisterStampRequest stampRequest,
            List<String> imgList,
            String userId,
            Long missionId) {
        return Stamp.builder()
                .contents(stampRequest.getContents())
                .createdAt(LocalDateTime.now())
                .images(imgList)
                .missionId(missionId)
                .userId(Long.valueOf(userId))
                .build();
    }

}
