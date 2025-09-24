package org.sopt.app.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.sopt.app.common.fixtures.SoptampFixture.*;

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
import org.sopt.app.common.event.EventPublisher;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.fixtures.SoptampFixture;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.soptamp.Stamp;
import org.sopt.app.interfaces.postgres.StampRepository;
import org.sopt.app.presentation.stamp.StampRequest;
import org.sopt.app.presentation.stamp.StampRequest.EditStampRequest;
import org.sopt.app.presentation.stamp.StampRequest.RegisterStampRequest;
import org.springframework.util.StringUtils;

@ExtendWith(MockitoExtension.class)
class StampServiceTest {

    @Mock
    private StampRepository stampRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private StampService stampService;

    @Test
    @DisplayName("SUCCESS_UserId와 MissionId로 스탬프를 찾으면 StampInfo.Stamp DTO 반환")
    void SUCCESS_findStamp() {
        //given
        Long userId = 1L;
        Long missionId = 100L;
        final Stamp savedStamp = getSavedStamp(userId, missionId);

        //when
        StampInfo.Stamp result = stampService.findStamp(missionId, userId);

        //then
        StampInfo.Stamp expected = StampInfo.Stamp.from(savedStamp);
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("FAIL_스탬프를 찾지 못하면 예외가 정상적으로 발생")
    void FAIL_findStampBadRequest_whenStampNotFound() {
        //given
        final Long userId = -1L;
        final Long missionId = -1L;

        Mockito.when(stampRepository.findByUserIdAndMissionId(anyLong(), anyLong()))
            .thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> stampService.findStamp(userId, missionId))
            .isInstanceOf(BadRequestException.class)
            .satisfies(e -> {
                BadRequestException exception = (BadRequestException) e;
                assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.STAMP_NOT_FOUND);
            });
    }

//    @Test
//    @DisplayName("SUCCESS_스탬프가 request에서 보낸 내용의 이미지와 내용으로 등록되었는지 확인")
//    void SUCCESS_uploadStampDeprecated() {
//        // given
//
//        RegisterStampRequest stampRequest = SoptampFixture.getRegisterStampRequest();
//
//        Stamp stamp = Stamp.builder()
//                .id(1L)
//                .contents(STAMP_CONTENTS)
//                .images(STAMP_IMG_PATHS)
//                .missionId(MISSION_ID)
//                .userId(USER_ID)
//                .build();
//
//        //when
//        Mockito.when(stampRepository.save(any(Stamp.class))).thenReturn(stamp);
//
//        Stamp newStamp = stampRepository.save(stamp);
//        StampInfo.Stamp expected = StampInfo.Stamp.builder()
//                .id(newStamp.getId())
//                .missionId(newStamp.getMissionId())
//                .contents(newStamp.getContents())
//                .images(newStamp.getImages())
//                .createdAt(newStamp.getCreatedAt())
//                .updatedAt(newStamp.getUpdatedAt())
//                .build();
//        StampInfo.Stamp result = stampService.uploadStampDeprecated(
//                stampRequest, STAMP_IMG_PATHS, USER_ID, MISSION_ID);
//
//        //then
//        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
//    }
//
    @Test
    @DisplayName("SUCCESS_스탬프가 request에서 보낸 내용의 이미지와 내용으로 등록되었는지 확인")
    void SUCCESS_uploadStamp() {
        // given
        Long userId = 1L;
        RegisterStampRequest stampRequest = SoptampFixture.getRegisterStampRequest();

        Stamp stamp = Stamp.builder()
                .contents(stampRequest.getContents())
                .images(List.of(stampRequest.getImage()))
                .missionId(stampRequest.getMissionId())
                .userId(userId)
                .build();

        Mockito.when(stampRepository.save(any(Stamp.class))).thenReturn(stamp);

        //when
        StampInfo.Stamp result = stampService.uploadStamp(stampRequest, userId);

        //then
        StampInfo.Stamp expected = StampInfo.Stamp.from(stamp);
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

//    @Test
//    @DisplayName("SUCCESS_request에서 보낸 내용의 이미지와 내용의 스탬프 DTO를 잘 반환하는지 확인_DEPRECATED")
//    void SUCCESS_editStampContentsDeprecated() {
//        // given
//        StampRequest.EditStampRequest editStampRequest = SoptampFixture.getEditStampRequest();
//
//        //when
//        Stamp oldStamp = getSavedStamp();
//        StampInfo.Stamp expected = editStamp(oldStamp, editStampRequest, true);
//        StampInfo.Stamp result = stampService.editStampContentsDeprecated(editStampRequest, USER_ID, MISSION_ID);
//
//        //then
//        Assertions.assertEquals(expected.getId(), result.getId());
//    }
//
//    @Test
//    @DisplayName("SUCCESS_request의 contents가 빈 문자열이면 contents를 변경하지 않음_DEPRECATED")
//    void SUCCESS_editStampContentsDeprecatedNoChangeContents() {
//        // given
//        StampRequest.EditStampRequest editStampRequest = SoptampFixture.getEditStampRequest();
//
//        //when
//        Stamp oldStamp = getSavedStamp();
//        editStamp(oldStamp, editStampRequest, true);
//
//        StampInfo.Stamp result = stampService.editStampContentsDeprecated(editStampRequest, USER_ID, MISSION_ID);
//
//        //then
//        Assertions.assertEquals(oldStamp.getId(), result.getId());
//    }
//

//

//
//    @Test
//    @DisplayName("FAIL_스탬프를 찾지 못하면 BadRequestException 발생_DEPRECATED")
//    void FAIL_editStampContentsDeprecated() {
//        //given
//        final StampRequest.EditStampRequest editStampRequest = SoptampFixture.getEditStampRequest();
//
//        //when
//        Mockito.when(stampRepository.findByUserIdAndMissionId(anyLong(), anyLong()))
//                .thenReturn(Optional.empty());
//
//        //then
//        Assertions.assertThrows(BadRequestException.class,
//                () -> stampService.editStampContentsDeprecated(editStampRequest, USER_ID, MISSION_ID));
//    }
//

    @Test
    @DisplayName("SUCCESS_request에서 보낸 내용의 이미지와 내용을 기존 스탬프에 정상적으로 업데이트 함")
    void SUCCESS_editStampContents() {
        // given
        Long missionId = 100L;
        Long userId = 1L;

        StampRequest.EditStampRequest editStampRequest = SoptampFixture.getEditStampRequestWithEdited(missionId);

        final Optional<Stamp> oldStamp = Optional.of(getStamp(userId, missionId));
        Mockito.when(stampRepository.findByUserIdAndMissionId(userId, missionId)).thenReturn(oldStamp);

        //when
        StampInfo.Stamp result = stampService.editStampContents(editStampRequest, userId);

        //then
        assertThat(result.getId()).isEqualTo(oldStamp.get().getId());
        assertThat(oldStamp.get())
            .extracting( "missionId", "userId", "contents", "images",  "activityDate")
            .contains(missionId, userId, List.of(EDITED_STAMP_IMAGE), EDITED_STAMP_CONTENTS, EDITED_STAMP_ACTIVITY_DATE);
    }

    @Test
    @DisplayName("SUCCESS_request의 contents가 빈 문자열이면 contents를 변경하지 않음")
    void SUCCESS_editStampContents_whenNoContents() {
        // given
        Long userId = 1L;
        Long missionId = 100L;
        StampRequest.EditStampRequest editStampRequest =
            new EditStampRequest(missionId, EDITED_STAMP_IMAGE, "", EDITED_STAMP_ACTIVITY_DATE);

        final Optional<Stamp> oldStamp = Optional.of(getStamp(userId, missionId));
        String oldContents = oldStamp.get().getContents();
        Mockito.when(stampRepository.findByUserIdAndMissionId(userId, missionId)).thenReturn(oldStamp);

        //when
        StampInfo.Stamp result = stampService.editStampContents(editStampRequest, userId);

        //then
        assertThat(result.getId()).isEqualTo(oldStamp.get().getId());
        assertThat(oldStamp.get())
            .extracting( "missionId", "userId", "contents", "images",  "activityDate")
            .contains(missionId, userId, List.of(EDITED_STAMP_IMAGE), oldContents, EDITED_STAMP_ACTIVITY_DATE);
    }

    @Test
    @DisplayName("SUCCESS_request의 image가 빈 문자열이면 image를 변경하지 않음")
    void SUCCESS_editStampContents_whenNoImage() {
        // given
        Long userId = 1L;
        Long missionId = 100L;
        StampRequest.EditStampRequest editStampRequest =
            new EditStampRequest(missionId, "", EDITED_STAMP_CONTENTS, EDITED_STAMP_ACTIVITY_DATE);

        final Optional<Stamp> oldStamp = Optional.of(getStamp(userId, missionId));
        List<String> oldImages = oldStamp.get().getImages();
        Mockito.when(stampRepository.findByUserIdAndMissionId(userId, missionId)).thenReturn(oldStamp);

        //when
        StampInfo.Stamp result = stampService.editStampContents(editStampRequest, userId);

        //then
        assertThat(result.getId()).isEqualTo(oldStamp.get().getId());
        assertThat(oldStamp.get())
            .extracting( "missionId", "userId", "contents", "images",  "activityDate")
            .contains(missionId, userId, oldImages, EDITED_STAMP_CONTENTS, EDITED_STAMP_ACTIVITY_DATE);
    }

    @Test
    @DisplayName("FAIL_스탬프를 찾지 못하면 BadRequestException 발생")
    void FAIL_editStampContents_whenStampNotFound() {
        //given
        final Long userId = -1L;
        final Long missionId = -100L;
        final StampRequest.EditStampRequest editStampRequest = SoptampFixture.getEditStampRequestWithEdited(missionId);

        Mockito.when(stampRepository.findByUserIdAndMissionId(anyLong(), anyLong()))
            .thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> stampService.editStampContents(editStampRequest, userId))
            .isInstanceOf(BadRequestException.class)
            .satisfies(e -> {
               BadRequestException badRequestException = (BadRequestException) e;
               assertThat(badRequestException.getErrorCode()).isEqualTo(ErrorCode.STAMP_NOT_FOUND);
            });
    }
//
//    @Test
//    @DisplayName("SUCCESS_스탬프 이미지 변경")
//    void SUCCESS_editStampImagesDeprecated() {
//        // given
//        final List<String> imgPaths = List.of("requestImage");
//
//        Stamp oldStamp = Stamp.builder()
//                .id(1L)
//                .contents("oldContents")
//                .images(List.of("oldImage"))
//                .build();
//
//        StampInfo.Stamp oldStampInfo = StampInfo.Stamp.builder()
//                .id(oldStamp.getId())
//                .contents(oldStamp.getContents())
//                .images(oldStamp.getImages())
//                .createdAt(oldStamp.getCreatedAt())
//                .updatedAt(oldStamp.getUpdatedAt())
//                .build();
//
//        //when
//        Mockito.when(stampRepository.findById(anyLong())).thenReturn(Optional.of(oldStamp));
//        oldStamp.changeImages(imgPaths);
//
//        //then
//        Assertions.assertDoesNotThrow(() -> {
//            stampService.editStampImagesDeprecated(oldStampInfo, imgPaths);
//        });
//    }
//
//
//    @Test
//    @DisplayName("FAIL_스탬프를 찾지 못하면 BadRequestException 발생")
//    void FAIL_editStampImagesDeprecated() {
//        // given
//        final List<String> imgPaths = List.of("requestImage");
//
//        Stamp oldStamp = Stamp.builder()
//                .id(1L)
//                .contents("oldContents")
//                .images(List.of("oldImage"))
//                .build();
//
//        StampInfo.Stamp oldStampInfo = StampInfo.Stamp.builder()
//                .id(oldStamp.getId())
//                .contents(oldStamp.getContents())
//                .images(oldStamp.getImages())
//                .createdAt(oldStamp.getCreatedAt())
//                .updatedAt(oldStamp.getUpdatedAt())
//                .build();
//
//        //when
//        Mockito.when(stampRepository.findById(anyLong())).thenReturn(Optional.empty());
//
//        //then
//        Assertions.assertThrows(BadRequestException.class, () -> {
//            stampService.editStampImagesDeprecated(oldStampInfo, imgPaths);
//        });
//    }
//
    @Test
    @DisplayName("SUCCESS_중복된 스탬프가 없으면 오류가 발생하지 않음")
    void SUCCESS_checkDuplicateStamp() {
        //given
        final Long userId = 1L;
        final Long missionId = 100L;

        Mockito.when(stampRepository.findByUserIdAndMissionId(anyLong(), anyLong()))
            .thenReturn(Optional.empty());

        //when & then
        Assertions.assertDoesNotThrow(() -> stampService.checkDuplicateStamp(userId, missionId));
    }

    @Test
    @DisplayName("FAIL_중복된 스탬프가 있으면 BadRequestException 발생")
    void FAIL_checkDuplicateStamp() {
        //given
        final Long userId = 1L;
        final Long missionId = 100L;

        //when
        Mockito.when(stampRepository.findByUserIdAndMissionId(anyLong(), anyLong()))
                .thenReturn(Optional.of(new Stamp()));

        //then
        assertThatThrownBy(() -> stampService.checkDuplicateStamp(userId, missionId))
            .isInstanceOf(BadRequestException.class)
            .satisfies(e -> {
               BadRequestException badRequestException = (BadRequestException) e;
               assertThat(badRequestException.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_STAMP);
            });
    }

//
//    @Test
//    @DisplayName("SUCCESS_스탬프를 찾으면 삭제")
//    void SUCCESS_deleteStampById() {
//        //given
//        final Long stampId = anyLong();
//
//        //when
//        Mockito.when(stampRepository.findById(stampId)).thenReturn(Optional.of(new Stamp()));
//
//        //then
//        Assertions.assertDoesNotThrow(() -> {
//            stampService.deleteStampById(stampId);
//        });
//    }
//
//    @Test
//    void SUCCESS_모든_스탬프_삭제시_스탬프_삭제_이벤트_발생(){
//        // given
//        final Long userId = anyLong();
//
//        // when
//        stampService.deleteAllStamps(userId);
//
//        // then
//        verify(eventPublisher).raise(any());
//    }
//
//    @Test
//    @DisplayName("FAIL_스탬프를 찾지 못하면 BadRequestException 발생")
//    void FAIL_deleteStampById() {
//        //given
//        final Long stampId = anyLong();
//
//        //when
//        Mockito.when(stampRepository.findById(stampId)).thenReturn(Optional.empty());
//
//        //then
//        Assertions.assertThrows(BadRequestException.class, () -> {
//            stampService.deleteStampById(stampId);
//        });
//    }
//
//    @Test
//    void SUCCESS_스탬프_삭제시_스탬프_삭제_이벤트_발생(){
//        // given
//        final Stamp stamp = Stamp.builder().build();
//        final Long stampId = anyLong();
//        given(stampRepository.findById(stampId)).willReturn(Optional.of(stamp));
//
//        // when
//        stampService.deleteStampById(stampId);
//
//        // then
//        verify(eventPublisher).raise(any());
//    }
//
//    @Test
//    @DisplayName("SUCCESS_모든 스탬프 삭제")
//    void SUCCESS_deleteAllStamps() {
//        //given
//        final Long anyUserId = anyLong();
//
//        //then
//        Assertions.assertDoesNotThrow(() -> stampService.deleteAllStamps(anyUserId));
//    }
//
//    @Test
//    @DisplayName("SUCCESS_스탬프 아이디로 미션 아이디 조회")
//    void SUCCESS_getMissionIdByStampId() {
//        //given
//        final Long anyStampId = anyLong();
//        final Long savedMissionId = 1L;
//
//        //when
//        Stamp savedStamp = Stamp.builder().missionId(savedMissionId).build();
//        Mockito.when(stampRepository.findById(anyStampId)).thenReturn(Optional.of(savedStamp));
//
//        //then
//        Assertions.assertEquals(savedMissionId, stampService.getMissionIdByStampId(anyStampId));
//    }
//
//    @Test
//    @DisplayName("FAIL_스탬프 아이디로 미션 아이디 조회되지 않을 시 BadRequestException 발생")
//    void FAIL_getMissionIdByStampId() {
//        //given
//        final Long anyStampId = anyLong();
//
//        //when
//        Mockito.when(stampRepository.findById(anyStampId)).thenReturn(Optional.empty());
//
//        //then
//        Assertions.assertThrows(BadRequestException.class, () -> {
//            stampService.getMissionIdByStampId(anyStampId);
//        });
//    }


    private Stamp getSavedStamp(Long userId, Long missionId) {
        final Optional<Stamp> savedStamp = Optional.of(getStamp(userId, missionId));

        Mockito.when(stampRepository.findByUserIdAndMissionId(userId, missionId)).thenReturn(savedStamp);

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

        return StampInfo.Stamp.builder()
                .id(newStamp.getId())
                .missionId(newStamp.getMissionId())
                .contents(newStamp.getContents())
                .images(newStamp.getImages())
                .createdAt(newStamp.getCreatedAt())
                .updatedAt(changedUpdatedAt)
                .build();
    }

//    private StampInfo.Stamp editStamp(Stamp oldStamp, StampRequest.EditStampRequest editStampRequest,
//            boolean isDeprecatedEditStampContents) {
//        if (!isDeprecatedEditStampContents && StringUtils.hasText(editStampRequest.getContents())) {
//            oldStamp.changeContents(editStampRequest.getContents());
//        }
//
//        if (StringUtils.hasText(editStampRequest.getImage())) {
//            oldStamp.changeImages(List.of(editStampRequest.getImage()));
//
//        }
//
//        final LocalDateTime changedUpdatedAt = LocalDateTime.of(2024, 2, 2, 0, 0, 0);
//
//        final Stamp newStamp = Stamp.builder()
//                .id(oldStamp.getId())
//                .contents(oldStamp.getContents())
//                .images(oldStamp.getImages())
//                .missionId(oldStamp.getMissionId())
//                .userId(oldStamp.getUserId())
//                .build();
//
//        return StampInfo.Stamp.builder()
//                .id(newStamp.getId())
//                .missionId(newStamp.getMissionId())
//                .contents(newStamp.getContents())
//                .images(newStamp.getImages())
//                .createdAt(newStamp.getCreatedAt())
//                .updatedAt(changedUpdatedAt)
//                .build();
//    }

}