package org.sopt.app.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.stamp.StampInfo;
import org.sopt.app.application.stamp.StampService;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.domain.entity.Stamp;
import org.sopt.app.interfaces.postgres.StampRepository;
import org.sopt.app.presentation.stamp.StampRequest.RegisterStampRequest;

@ExtendWith(MockitoExtension.class)
class StampServiceTest {

    @Mock
    private StampRepository stampRepository;

    @InjectMocks
    private StampService stampService;

    @Test
    @DisplayName("SUCCESS_UserId와 MissionId로 스탬프를 찾으면 StampInfo.Stamp DTO 반환")
    void SUCCESS_findStamp() {
        //given
        val stampUserId = anyLong();
        val stampMissionId = anyLong();
        val stampId = 1L;
        val stamp = Stamp.builder().id(stampId).userId(stampUserId).missionId(stampMissionId).build();

        //when
        Mockito.when(stampRepository.findByUserIdAndMissionId(stampUserId, stampMissionId))
                .thenReturn(Optional.of(stamp));
        StampInfo.Stamp expected = StampInfo.Stamp.builder()
                .id(stamp.getId())
                .contents(stamp.getContents())
                .images(stamp.getImages())
                .createdAt(stamp.getCreatedAt())
                .updatedAt(stamp.getUpdatedAt())
                .build();
        StampInfo.Stamp result = stampService.findStamp(stampUserId, stampMissionId);

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
        Assertions.assertInstanceOf(StampInfo.Stamp.class, result);
    }

    @Test
    @DisplayName("FAIL_스탬프를 찾지 못하면 BadRequestException 발생")
    void FAIL_findStampBadRequest() {
        //given
        val anyUserId = anyLong();
        val anyMissionId = anyLong();

        //when
        Mockito.when(stampRepository.findByUserIdAndMissionId(anyUserId, anyMissionId))
                .thenReturn(Optional.empty());

        //then
        Assertions.assertThrows(BadRequestException.class, () -> {
            stampService.findStamp(anyUserId, anyMissionId);
        });
    }

    @Test
    @DisplayName("SUCCESS_스탬프가 request에서 보낸 내용의 이미지와 내용으로 변경되었는지 확인")
    void SUCCESS_uploadStampDeprecated() {
        // given
        val requestUserId = 1L;
        val requestMissionId = 1L;
        val requestContents = "requestContents";
        List<String> imgPaths = List.of("requestImage1", "requestImage2");
        RegisterStampRequest stampRequest = new RegisterStampRequest();
        stampRequest.setContents(requestContents);
        val stamp = this.convertStampImgDeprecated(stampRequest, imgPaths, requestUserId, requestMissionId);

        //when
        Mockito.when(stampRepository.save(stamp)).thenReturn(stamp);
        val newStamp = stampRepository.save(stamp);
        StampInfo.Stamp expected = StampInfo.Stamp.builder()
                .id(newStamp.getId())
                .contents(newStamp.getContents())
                .images(newStamp.getImages())
                .createdAt(newStamp.getCreatedAt())
                .updatedAt(newStamp.getUpdatedAt())
                .build();

        //then
        Assertions.assertEquals(expected.getId(), stamp.getId());
        Assertions.assertEquals(expected.getImages(), imgPaths);
        Assertions.assertEquals(expected.getContents(), requestContents);
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

    /* TODO: Implement the following tests
    @Test
    void SUCCESS_uploadStamp() {
    }

    @Test
    void editStampContentsDeprecated() {
    }

    @Test
    void editStampContents() {
    }

    @Test
    void editStampImagesDeprecated() {
    }

    @Test
    void checkDuplicateStamp() {
    }

    @Test
    void deleteStampById() {
    }

    @Test
    void deleteAllStamps() {
    }

    @Test
    void getMissionIdByStampId() {
    }

 */
}