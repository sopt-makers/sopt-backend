package org.sopt.app.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
import org.sopt.app.presentation.stamp.StampRequest;
import org.sopt.app.presentation.stamp.StampRequest.RegisterStampRequest;
import org.springframework.util.StringUtils;

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
        final Long stampUserId = anyLong();
        final Long stampMissionId = anyLong();
        final Long stampId = 1L;
        final Stamp stamp = Stamp.builder().id(stampId).userId(stampUserId).missionId(stampMissionId).build();

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
        final Long anyUserId = anyLong();
        final Long anyMissionId = anyLong();

        //when
        Mockito.when(stampRepository.findByUserIdAndMissionId(anyUserId, anyMissionId))
                .thenReturn(Optional.empty());

        //then
        Assertions.assertThrows(BadRequestException.class, () -> {
            stampService.findStamp(anyUserId, anyMissionId);
        });
    }

    @Test
    @DisplayName("SUCCESS_스탬프가 request에서 보낸 내용의 이미지와 내용으로 등록되었는지 확인")
    void SUCCESS_uploadStampDeprecated() {
        // given
        final Long requestUserId = 1L;
        final Long requestMissionId = 1L;
        final String requestContents = "requestContents";
        final List<String> imgPaths = List.of("requestImage1", "requestImage2");

        RegisterStampRequest stampRequest = new RegisterStampRequest();
        stampRequest.setContents(requestContents);

        Stamp stamp = Stamp.builder()
                .id(1L)
                .contents(stampRequest.getContents())
                .createdAt(LocalDateTime.now())
                .images(imgPaths)
                .missionId(requestMissionId)
                .userId(requestUserId)
                .build();

        //when
        Mockito.when(stampRepository.save(any(Stamp.class))).thenReturn(stamp);

        Stamp newStamp = stampRepository.save(stamp);
        StampInfo.Stamp expected = StampInfo.Stamp.builder()
                .id(newStamp.getId())
                .contents(newStamp.getContents())
                .images(newStamp.getImages())
                .createdAt(newStamp.getCreatedAt())
                .updatedAt(newStamp.getUpdatedAt())
                .build();
        StampInfo.Stamp result = stampService.uploadStampDeprecated(stampRequest, imgPaths, requestUserId,
                requestMissionId);

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("SUCCESS_스탬프가 request에서 보낸 내용의 이미지와 내용으로 등록되었는지 확인")
    void SUCCESS_uploadStamp() {
        // given
        final Long requestUserId = 1L;
        final Long requestMissionId = 1L;
        final String requestContents = "requestContents";
        final String requestImage = "requestImage";

        RegisterStampRequest stampRequest = new RegisterStampRequest();
        stampRequest.setContents(requestContents);
        stampRequest.setImage(requestImage);
        stampRequest.setMissionId(requestMissionId);

        Stamp stamp = Stamp.builder()
                .contents(stampRequest.getContents())
                .createdAt(LocalDateTime.now())
                .images(List.of(stampRequest.getImage()))
                .missionId(stampRequest.getMissionId())
                .userId(requestUserId)
                .build();

        //when
        Mockito.when(stampRepository.save(any(Stamp.class))).thenReturn(stamp);
        Stamp newStamp = stampRepository.save(stamp);
        StampInfo.Stamp expected = StampInfo.Stamp.builder()
                .id(newStamp.getId())
                .contents(newStamp.getContents())
                .images(newStamp.getImages())
                .createdAt(newStamp.getCreatedAt())
                .updatedAt(newStamp.getUpdatedAt())
                .build();

        StampInfo.Stamp result = stampService.uploadStamp(stampRequest, requestUserId);

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("SUCCESS_스탬프가 request에서 보낸 내용의 이미지와 내용으로 변경되었는지 확인_DEPRECATED")
    void SUCCESS_editStampContentsDeprecated() {
        // given
        final Long requestUserId = anyLong();
        final Long requestMissionId = anyLong();
        final String requestContents = "requestContents";
        final String requestImage = "requestImage";

        StampRequest.EditStampRequest editStampRequest = new StampRequest.EditStampRequest();
        editStampRequest.setContents(requestContents);
        editStampRequest.setImage(requestImage);

        //when
        StampInfo.Stamp expected = getExpectedEditStampDeprecated(editStampRequest, requestUserId, requestMissionId);
        StampInfo.Stamp result = stampService.editStampContentsDeprecated(editStampRequest, requestUserId, requestMissionId);

        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("SUCCESS_request의 contents가 빈 문자열이면 contents를 변경하지 않음_DEPRECATED")
    void SUCCESS_editStampContentsDeprecatedNoChangeContents() {
        // given
        final Long requestUserId = anyLong();
        final Long requestMissionId = anyLong();

        final String requestBlankContents = "";
        final String requestImage = "requestImage";
        StampRequest.EditStampRequest editStampRequest = new StampRequest.EditStampRequest();
        editStampRequest.setContents(requestBlankContents);
        editStampRequest.setImage(requestImage);

        //when
        StampInfo.Stamp expected = getExpectedEditStampDeprecated(editStampRequest, requestUserId, requestMissionId);
        StampInfo.Stamp result = stampService.editStampContentsDeprecated(editStampRequest, requestUserId, requestMissionId);

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    private StampInfo.Stamp getExpectedEditStampDeprecated(StampRequest.EditStampRequest editStampRequest, Long requestUserId, Long requestMissionId) {
        final Long stampId = 1L;
        final LocalDateTime createdAt = LocalDateTime.of(2024,1,1,0,0,0);
        final LocalDateTime unchangedUpdatedAt = LocalDateTime.of(2024,1,1,0,0,0);

        final Optional<Stamp> oldStamp = Optional.of(Stamp.builder()
                .id(stampId)
                .contents("oldContents")
                .images(List.of("oldImage"))
                .createdAt(createdAt)
                .updatedAt(unchangedUpdatedAt)
                .missionId(requestMissionId)
                .userId(requestUserId)
                .build());

        Mockito.when(stampRepository.findByUserIdAndMissionId(requestUserId, requestMissionId)).thenReturn(oldStamp);

        final LocalDateTime changedUpdatedAt = LocalDateTime.of(2024,2,2,0,0,0);

        if (!StringUtils.hasText(editStampRequest.getContents())) {
            editStampRequest.setContents(oldStamp.get().getContents());
        }

        final Stamp newStamp = Stamp.builder()
                .id(stampId)
                .contents(editStampRequest.getContents())
                .images(List.of(editStampRequest.getImage()))
                .createdAt(createdAt)
                .updatedAt(changedUpdatedAt)
                .missionId(requestMissionId)
                .userId(requestUserId)
                .build();

        Mockito.when(stampRepository.save(any(Stamp.class))).thenReturn(newStamp);

        return StampInfo.Stamp.builder()
                .id(stampId)
                .contents(editStampRequest.getContents())
                .images(List.of(editStampRequest.getImage()))
                .createdAt(createdAt)
                .updatedAt(changedUpdatedAt)
                .build();
    }

    @Test
    @DisplayName("FAIL_스탬프를 찾지 못하면 BadRequestException 발생_DEPRECATED")
    void FAIL_editStampContentsDeprecated() {
        //given
        final Long requestUserId = anyLong();
        final Long requestMissionId = anyLong();
        final StampRequest.EditStampRequest editStampRequest = new StampRequest.EditStampRequest();

        //when
        Mockito.when(stampRepository.findByUserIdAndMissionId(requestUserId, requestMissionId)).thenReturn(Optional.empty());

        //then
        Assertions.assertThrows(BadRequestException.class, () -> {
            stampService.editStampContentsDeprecated(editStampRequest, requestUserId, requestMissionId);
        });
    }


    @Test
    void SUCCESS_editStampContents() {

    }

    /* TODO: Implement the following tests
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