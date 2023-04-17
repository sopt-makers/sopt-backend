package org.sopt.app.application.stamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.Stamp;
import org.sopt.app.domain.entity.User;
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
    public Stamp findStamp(Long userId, Long missionId) {
        return stampRepository.findByUserIdAndMissionId(userId, missionId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.STAMP_NOT_FOUND.getMessage()));
    }

    @Transactional
    public Stamp uploadStampDeprecated(
            RegisterStampRequest stampRequest,
            List<String> imgPaths,
            Long userId,
            Long missionId) {
        val imgList = new ArrayList<>(imgPaths);
        val stamp = this.convertStampImgDeprecated(stampRequest, imgList, userId, missionId);

        val user = userRepository.findUserById(Long.valueOf(userId))
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND.getMessage()));
        val mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.MISSION_NOT_FOUND.getMessage()));

        user.addPoints(mission.getLevel());
        userRepository.save(user);

        return stampRepository.save(stamp);
    }

    @Transactional
    public Stamp uploadStamp(
            RegisterStampRequest stampRequest,
            User user,
            Long missionId) {

        val mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.MISSION_NOT_FOUND.getMessage()));
        val stamp = Stamp.builder()
                .contents(stampRequest.getContents())
                .createdAt(LocalDateTime.now())
                .images(List.of(stampRequest.getImage()))
                .missionId(missionId)
                .userId(user.getId())
                .build();
        user.addPoints(mission.getLevel());
        userRepository.save(user);

        return stampRepository.save(stamp);
    }

    //스탬프 내용 수정
    @Transactional
    public Stamp editStampContentsDeprecated(
            StampRequest.EditStampRequest editStampRequest,
            Long userId,
            Long missionId) {

        val stamp = stampRepository.findByUserIdAndMissionId(userId, missionId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.STAMP_NOT_FOUND.getMessage()));
        if (StringUtils.hasText(editStampRequest.getContents())) {
            stamp.changeContents(editStampRequest.getContents());
        }

        stamp.setUpdatedAt(LocalDateTime.now());
        return stampRepository.save(stamp);
    }

    @Transactional
    public Stamp editStampContents(
            StampRequest.EditStampRequest editStampRequest,
            Long userId,
            Long missionId) {

        val stamp = stampRepository.findByUserIdAndMissionId(userId, missionId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.STAMP_NOT_FOUND.getMessage()));
        if (StringUtils.hasText(editStampRequest.getContents())) {
            stamp.changeContents(editStampRequest.getContents());
        }
        if (StringUtils.hasText(editStampRequest.getImage())) {
            stamp.changeImages(List.of(editStampRequest.getImage()));
        }
        stamp.setUpdatedAt(LocalDateTime.now());
        return stampRepository.save(stamp);
    }

    //스탬프 사진 수정
    @Transactional
    public Stamp editStampImagesDeprecated(Stamp stamp, List<String> imgPaths) {
        stamp.changeImages(imgPaths);
        return stampRepository.save(stamp);
    }

    //Stamp 삭제 by stampId
    @Transactional
    public List<String> deleteStampById(User user, Long stampId) {

        val stamp = stampRepository.findById(stampId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.STAMP_NOT_FOUND.getMessage()));
        val mission = missionRepository.findById(stamp.getMissionId())
                .orElseThrow(() -> new BadRequestException(ErrorCode.MISSION_NOT_FOUND.getMessage()));

        user.minusPoints(mission.getLevel());
        userRepository.save(user);
        stampRepository.deleteById(stampId);
        return stamp.getImages();
    }


    @Transactional(readOnly = true)
    public void checkDuplicateStamp(Long userId, Long missionId) {
        if (stampRepository.findByUserIdAndMissionId(userId, missionId).isPresent()) {
            throw new BadRequestException(ErrorCode.DUPLICATE_STAMP.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<String> findAllStamps(User user) {
        return stampRepository.findAllByUserId(user.getId()).stream().map(Stamp::getImages)
                .flatMap(images -> images.stream()).collect(Collectors.toList());
    }


    @Transactional
    public void deleteAllStamps(User user) {
        stampRepository.deleteAllByUserId(user.getId());
        user.initializePoints();
        userRepository.save(user);
    }


    private Stamp convertStampImgDeprecated(
            RegisterStampRequest stampRequest,
            List<String> imgList,
            Long userId,
            Long missionId) {
        return Stamp.builder()
                .contents(stampRequest.getContents())
                .createdAt(LocalDateTime.now())
                .images(imgList)
                .missionId(missionId)
                .userId(userId)
                .build();
    }

}
