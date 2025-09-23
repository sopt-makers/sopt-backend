package org.sopt.app.application.stamp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.sopt.app.application.user.UserWithdrawEvent;
import org.sopt.app.common.event.EventPublisher;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.exception.ForbiddenException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.soptamp.Stamp;
import org.sopt.app.interfaces.postgres.StampRepository;
import org.sopt.app.presentation.stamp.StampRequest;
import org.sopt.app.presentation.stamp.StampRequest.RegisterStampRequest;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class StampService {

    private final StampRepository stampRepository;
    private final EventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public StampInfo.Stamp findStamp(Long missionId, Long userId) {
        val entity = stampRepository.findByUserIdAndMissionId(userId, missionId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.STAMP_NOT_FOUND));
        entity.validate();
        return StampInfo.Stamp.from(entity);
    }

    @Transactional
    public StampInfo.Stamp uploadStampDeprecated(
            @Valid RegisterStampRequest stampRequest,
            List<String> imgPaths,
            Long userId,
            Long missionId) {
        val imgList = new ArrayList<>(imgPaths);
        val stamp = this.convertStampImgDeprecated(stampRequest, imgList, userId, missionId);
        val newStamp = stampRepository.save(stamp);

        return StampInfo.Stamp.from(newStamp);
    }

    @Transactional
    public StampInfo.Stamp uploadStamp(
            @Valid RegisterStampRequest stampRequest,
            Long userId) {
        val stamp = Stamp.builder()
                .contents(stampRequest.getContents())
                .images(List.of(stampRequest.getImage()))
                .missionId(stampRequest.getMissionId())
                .activityDate(stampRequest.getActivityDate())
                .userId(userId)
                .build();

        val newStamp = stampRepository.save(stamp);
        return StampInfo.Stamp.from(newStamp);
    }

    @Transactional
    public StampInfo.Stamp editStampContentsDeprecated(
            @Valid StampRequest.EditStampRequest editStampRequest,
            Long userId,
            Long missionId) {

        val stamp = stampRepository.findByUserIdAndMissionId(userId, missionId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.STAMP_NOT_FOUND));
        if (StringUtils.hasText(editStampRequest.getContents())) {
            stamp.changeContents(editStampRequest.getContents());
        }

        return StampInfo.Stamp.builder()
                .id(stamp.getId())
                .build();
    }

    @Transactional
    public StampInfo.Stamp editStampContents(
            @Valid StampRequest.EditStampRequest editStampRequest,
            Long userId) {

        val stamp = stampRepository.findByUserIdAndMissionId(userId, editStampRequest.getMissionId())
                .orElseThrow(() -> new BadRequestException(ErrorCode.STAMP_NOT_FOUND));
        if (StringUtils.hasText(editStampRequest.getContents())) {
            stamp.changeContents(editStampRequest.getContents());
        }
        if (StringUtils.hasText(editStampRequest.getImage())) {
            stamp.changeImages(List.of(editStampRequest.getImage()));
        }
        if (editStampRequest.getActivityDate() == null) {
            throw new BadRequestException(ErrorCode.INVALID_STAMP_ACTIVITY_DATE);
        }
        stamp.changeActivityDate(editStampRequest.getActivityDate());
        return StampInfo.Stamp.builder()
                .id(stamp.getId())
                .build();
    }

    @Transactional
    public void editStampImagesDeprecated(StampInfo.Stamp stamp, List<String> imgPaths) {
        val oldStamp = stampRepository.findById(stamp.getId())
                .orElseThrow(() -> new BadRequestException(ErrorCode.STAMP_NOT_FOUND));
        oldStamp.changeImages(imgPaths);
    }

    @Transactional(readOnly = true)
    public void checkDuplicateStamp(Long userId, Long missionId) {
        if (stampRepository.findByUserIdAndMissionId(userId, missionId).isPresent()) {
            throw new BadRequestException(ErrorCode.DUPLICATE_STAMP);
        }
    }

    @Transactional
    public void deleteStampById(Long stampId) {

        val stamp = stampRepository.findById(stampId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.STAMP_NOT_FOUND));
        stampRepository.deleteById(stampId);

        eventPublisher.raise(new StampDeletedEvent(stamp.getImages()));
    }

    @Transactional
    public void deleteAllStamps(Long userId) {
        stampRepository.deleteAllByUserId(userId);

        val imageUrls = stampRepository.findAllByUserId(userId).stream().map(Stamp::getImages)
                .flatMap(Collection::stream).toList();
        eventPublisher.raise(new StampDeletedEvent(imageUrls));
    }

    @EventListener(UserWithdrawEvent.class)
    public void handleUserWithdrawEvent(final UserWithdrawEvent event) {
        this.deleteAllStamps(event.getUserId());
    }

    private Stamp convertStampImgDeprecated(
            @Valid RegisterStampRequest stampRequest,
            List<String> imgList,
            Long userId,
            Long missionId) {
        return Stamp.builder()
                .contents(stampRequest.getContents())
                .images(imgList)
                .missionId(missionId)
                .userId(userId)
                .build();
    }

    public Long getMissionIdByStampId(Long stampId) {
        return stampRepository.findById(stampId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.STAMP_NOT_FOUND))
                .getMissionId();
    }

    @Transactional(readOnly = true)
    public Stamp getStampForDelete(Long stampId, Long userId) {
        return stampRepository.findByIdAndUserId(stampId, userId)
            .orElseThrow(() -> new ForbiddenException(ErrorCode.STAMP_DELETE_FORBIDDEN));
    }

    public void deleteAll() {
        stampRepository.deleteAll();
    }
}
