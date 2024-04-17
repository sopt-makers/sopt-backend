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
        final Stamp stamp = getSavedStamp(stampMissionId, stampUserId);

        //when
        StampInfo.Stamp expected = StampInfo.Stamp.builder()
                .id(stamp.getId())
                .contents(stamp.getContents())
                .images(stamp.getImages())
                .activityDate(stamp.getActivityDate())
                .missionId(stamp.getMissionId())
                .build();
        StampInfo.Stamp result = stampService.findStamp(stampUserId, stampMissionId);

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
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
                .images(imgPaths)
                .missionId(requestMissionId)
                .userId(requestUserId)
                .build();

        //when
        Mockito.when(stampRepository.save(any(Stamp.class))).thenReturn(stamp);

        Stamp newStamp = stampRepository.save(stamp);
        StampInfo.Stamp expected = StampInfo.Stamp.builder()
                .id(newStamp.getId())
                .missionId(newStamp.getMissionId())
                .contents(newStamp.getContents())
                .images(newStamp.getImages())
                .createdAt(newStamp.getCreatedAt())
                .updatedAt(newStamp.getUpdatedAt())
                .build();
        StampInfo.Stamp result = stampService.uploadStampDeprecated(
                stampRequest, imgPaths, requestUserId, requestMissionId);

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
                .images(List.of(stampRequest.getImage()))
                .missionId(stampRequest.getMissionId())
                .userId(requestUserId)
                .build();

        //when
        Mockito.when(stampRepository.save(any(Stamp.class))).thenReturn(stamp);
        Stamp newStamp = stampRepository.save(stamp);
        StampInfo.Stamp expected = StampInfo.Stamp.builder()
                .id(newStamp.getId())
                .missionId(newStamp.getMissionId())
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
    @DisplayName("SUCCESS_request에서 보낸 내용의 이미지와 내용의 스탬프 DTO를 잘 반환하는지 확인_DEPRECATED")
    void SUCCESS_editStampContentsDeprecated() {
        // given
        final Long requestUserId = anyLong();
        final Long requestMissionId = anyLong();
        final String requestContents = "requestContents";
        final String requestImage = "requestImage";

        StampRequest.EditStampRequest editStampRequest = new StampRequest.EditStampRequest();
        editStampRequest.setContents(requestContents);
        editStampRequest.setImage(requestImage);
        editStampRequest.setMissionId(requestMissionId);

        //when
        Stamp oldStamp = getSavedStamp(requestMissionId, requestUserId);
        StampInfo.Stamp expected = editStamp(oldStamp, editStampRequest, true);
        StampInfo.Stamp result = stampService.editStampContentsDeprecated(editStampRequest, requestUserId,
                requestMissionId);

        //then
        Assertions.assertEquals(expected.getId(), result.getId());
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
        editStampRequest.setMissionId(requestMissionId);

        //when
        Stamp oldStamp = getSavedStamp(requestMissionId, requestUserId);
        editStamp(oldStamp, editStampRequest, true);

        StampInfo.Stamp result = stampService.editStampContentsDeprecated(editStampRequest, requestUserId,
                requestMissionId);

        //then
        Assertions.assertEquals(oldStamp.getId(), result.getId());
    }

    private Stamp getSavedStamp(Long missionId, Long requestUserId) {
        final Long stampId = 1L;
        final String contents = "savedContents";
        final List<String> images = List.of("savedImage");
        final String activityDate = "2024.04.08";

        final Optional<Stamp> savedStamp = Optional.of(Stamp.builder()
                .id(stampId)
                .contents(contents)
                .images(images)
                .missionId(missionId)
                .userId(requestUserId)
                .activityDate(activityDate)
                .build());

        Mockito.when(stampRepository.findByUserIdAndMissionId(requestUserId, missionId)).thenReturn(savedStamp);

        return savedStamp.get();
    }

    private StampInfo.Stamp editStamp(Stamp oldStamp, StampRequest.EditStampRequest editStampRequest,
            boolean isDeprecatedEditStampContents) {
        if (!isDeprecatedEditStampContents && StringUtils.hasText(editStampRequest.getContents())) {
            oldStamp.changeContents(editStampRequest.getContents());
        }

        if (StringUtils.hasText(editStampRequest.getImage())) {
            oldStamp.changeImages(List.of(editStampRequest.getImage()));

        }

        final LocalDateTime changedUpdatedAt = LocalDateTime.of(2024, 2, 2, 0, 0, 0);

        final Stamp newStamp = Stamp.builder()
                .id(oldStamp.getId())
                .contents(oldStamp.getContents())
                .images(oldStamp.getImages())
                .missionId(oldStamp.getMissionId())
                .userId(oldStamp.getUserId())
                .build();

        Mockito.when(stampRepository.save(any(Stamp.class))).thenReturn(newStamp);

        return StampInfo.Stamp.builder()
                .id(newStamp.getId())
                .missionId(newStamp.getMissionId())
                .contents(newStamp.getContents())
                .images(newStamp.getImages())
                .createdAt(newStamp.getCreatedAt())
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
        Mockito.when(stampRepository.findByUserIdAndMissionId(requestUserId, requestMissionId))
                .thenReturn(Optional.empty());

        //then
        Assertions.assertThrows(BadRequestException.class, () -> {
            stampService.editStampContentsDeprecated(editStampRequest, requestUserId, requestMissionId);
        });
    }


    @Test
    @DisplayName("SUCCESS_request에서 보낸 내용의 이미지와 내용의 스탬프 DTO를 잘 반환하는지 확인")
    void SUCCESS_editStampContents() {
// given
        final Long requestUserId = anyLong();
        final Long requestMissionId = anyLong();
        final String requestContents = "requestContents";
        final String requestImage = "requestImage";
        final String requestActivityDate = "2024.04.08";

        StampRequest.EditStampRequest editStampRequest = new StampRequest.EditStampRequest();
        editStampRequest.setContents(requestContents);
        editStampRequest.setImage(requestImage);
        editStampRequest.setMissionId(requestMissionId);
        editStampRequest.setActivityDate(requestActivityDate);

        //when
        Stamp oldStamp = getSavedStamp(requestMissionId, requestUserId);
        editStamp(oldStamp, editStampRequest, false);
        StampInfo.Stamp result = stampService.editStampContents(editStampRequest, requestUserId);

        //then
        Assertions.assertEquals(oldStamp.getId(), result.getId());
    }

    @Test
    @DisplayName("SUCCESS_SUCCESS_request의 contents가 빈 문자열이면 contents를 변경하지 않음")
    void SUCCESS_editStampContentsNoContents() {
        // given
        final Long requestUserId = anyLong();
        final Long requestMissionId = anyLong();

        final String requestBlankContents = "";
        final String requestImage = "requestImage";
        final String requestActivityDate = "2024.04.08";

        StampRequest.EditStampRequest editStampRequest = new StampRequest.EditStampRequest();
        editStampRequest.setContents(requestBlankContents);
        editStampRequest.setImage(requestImage);
        editStampRequest.setMissionId(requestMissionId);
        editStampRequest.setActivityDate(requestActivityDate);

        //when
        Stamp oldStamp = getSavedStamp(requestMissionId, requestUserId);
        StampInfo.Stamp expected = editStamp(oldStamp, editStampRequest, false);
        StampInfo.Stamp result = stampService.editStampContents(editStampRequest, requestUserId);

        //then
        Assertions.assertEquals(expected.getId(), result.getId());
    }

    @Test
    @DisplayName("SUCCESS_SUCCESS_request의 image가 빈 문자열이면 image를 변경하지 않음")
    void SUCCESS_editStampContentsNoImage() {
        // given
        final Long requestUserId = anyLong();
        final Long requestMissionId = anyLong();
        final String requestContents = "requestContents";
        final String requestBlankImage = "";
        final String requestActivityDate = "2024.04.08";

        StampRequest.EditStampRequest editStampRequest = new StampRequest.EditStampRequest();
        editStampRequest.setContents(requestContents);
        editStampRequest.setImage(requestBlankImage);
        editStampRequest.setMissionId(requestMissionId);
        editStampRequest.setActivityDate(requestActivityDate);

        //when
        Stamp oldStamp = getSavedStamp(requestMissionId, requestUserId);
        editStamp(oldStamp, editStampRequest, false);
        StampInfo.Stamp result = stampService.editStampContents(editStampRequest, requestUserId);

        //then
        Assertions.assertEquals(oldStamp.getId(), result.getId());
    }

    @Test
    @DisplayName("FAIL_스탬프를 찾지 못하면 BadRequestException 발생")
    void FAIL_editStampContents() {
        //given
        final Long requestUserId = anyLong();
        final Long requestMissionId = anyLong();
        final StampRequest.EditStampRequest editStampRequest = new StampRequest.EditStampRequest();
        editStampRequest.setMissionId(requestMissionId);

        //when
        Mockito.when(stampRepository.findByUserIdAndMissionId(requestUserId, requestMissionId))
                .thenReturn(Optional.empty());

        //then
        Assertions.assertThrows(BadRequestException.class, () -> {
            stampService.editStampContents(editStampRequest, requestUserId);
        });
    }

    @Test
    @DisplayName("SUCCESS_스탬프 이미지 변경")
    void SUCCESS_editStampImagesDeprecated() {
        // given
        final List<String> imgPaths = List.of("requestImage");

        Stamp oldStamp = Stamp.builder()
                .id(1L)
                .contents("oldContents")
                .images(List.of("oldImage"))
                .build();

        StampInfo.Stamp oldStampInfo = StampInfo.Stamp.builder()
                .id(oldStamp.getId())
                .contents(oldStamp.getContents())
                .images(oldStamp.getImages())
                .createdAt(oldStamp.getCreatedAt())
                .updatedAt(oldStamp.getUpdatedAt())
                .build();

        //when
        Mockito.when(stampRepository.findById(anyLong())).thenReturn(Optional.of(oldStamp));
        oldStamp.changeImages(imgPaths);
        Mockito.when(stampRepository.save(any(Stamp.class))).thenReturn(oldStamp);

        //then
        Assertions.assertDoesNotThrow(() -> {
            stampService.editStampImagesDeprecated(oldStampInfo, imgPaths);
        });
    }


    @Test
    @DisplayName("FAIL_스탬프를 찾지 못하면 BadRequestException 발생")
    void FAIL_editStampImagesDeprecated() {
        // given
        final List<String> imgPaths = List.of("requestImage");

        Stamp oldStamp = Stamp.builder()
                .id(1L)
                .contents("oldContents")
                .images(List.of("oldImage"))
                .build();

        StampInfo.Stamp oldStampInfo = StampInfo.Stamp.builder()
                .id(oldStamp.getId())
                .contents(oldStamp.getContents())
                .images(oldStamp.getImages())
                .createdAt(oldStamp.getCreatedAt())
                .updatedAt(oldStamp.getUpdatedAt())
                .build();

        //when
        Mockito.when(stampRepository.findById(anyLong())).thenReturn(Optional.empty());

        //then
        Assertions.assertThrows(BadRequestException.class, () -> {
            stampService.editStampImagesDeprecated(oldStampInfo, imgPaths);
        });
    }

    @Test
    @DisplayName("SUCCESS_중복된 스탬프가 없으면 오류가 발생하지 않음")
    void SUCCESS_checkDuplicateStamp() {
        //given
        final Long anyUserId = anyLong();
        final Long anyMissionId = anyLong();

        //when
        Mockito.when(stampRepository.findByUserIdAndMissionId(anyUserId, anyMissionId)).thenReturn(Optional.empty());

        //then
        Assertions.assertDoesNotThrow(() -> {
            stampService.checkDuplicateStamp(anyUserId, anyMissionId);
        });
    }

    @Test
    @DisplayName("SUCCESS_중복된 스탬프가 있으면 BadRequestException 발생")
    void FAIL_checkDuplicateStamp() {
        //given
        final Long anyUserId = anyLong();
        final Long anyMissionId = anyLong();

        //when
        Mockito.when(stampRepository.findByUserIdAndMissionId(anyUserId, anyMissionId))
                .thenReturn(Optional.of(new Stamp()));

        //then
        Assertions.assertThrows(BadRequestException.class, () -> {
            stampService.checkDuplicateStamp(anyUserId, anyMissionId);
        });
    }

    @Test
    @DisplayName("SUCCESS_스탬프를 찾으면 삭제")
    void SUCCESS_deleteStampById() {
        //given
        final Long stampId = anyLong();

        //when
        Mockito.when(stampRepository.findById(stampId)).thenReturn(Optional.of(new Stamp()));

        //then
        Assertions.assertDoesNotThrow(() -> {
            stampService.deleteStampById(stampId);
        });
    }

    @Test
    @DisplayName("FAIL_스탬프를 찾지 못하면 BadRequestException 발생")
    void FAIL_deleteStampById() {
        //given
        final Long stampId = anyLong();

        //when
        Mockito.when(stampRepository.findById(stampId)).thenReturn(Optional.empty());

        //then
        Assertions.assertThrows(BadRequestException.class, () -> {
            stampService.deleteStampById(stampId);
        });
    }

    @Test
    @DisplayName("SUCCESS_모든 스탬프 삭제")
    void SUCCESS_deleteAllStamps() {
        //given
        final Long anyUserId = anyLong();

        //then
        Assertions.assertDoesNotThrow(() -> {
            stampService.deleteAllStamps(anyUserId);
        });
    }

    @Test
    @DisplayName("SUCCESS_스탬프 아이디로 미션 아이디 조회")
    void SUCCESS_getMissionIdByStampId() {
        //given
        final Long anyStampId = anyLong();
        final Long savedMissionId = 1L;

        //when
        Stamp savedStamp = Stamp.builder().missionId(savedMissionId).build();
        Mockito.when(stampRepository.findById(anyStampId)).thenReturn(Optional.of(savedStamp));

        //then
        Assertions.assertEquals(savedMissionId, stampService.getMissionIdByStampId(anyStampId));
    }

    @Test
    @DisplayName("FAIL_스탬프 아이디로 미션 아이디 조회되지 않을 시 BadRequestException 발생")
    void FAIL_getMissionIdByStampId() {
        //given
        final Long anyStampId = anyLong();

        //when
        Mockito.when(stampRepository.findById(anyStampId)).thenReturn(Optional.empty());

        //then
        Assertions.assertThrows(BadRequestException.class, () -> {
            stampService.getMissionIdByStampId(anyStampId);
        });
    }


}