package org.sopt.app.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.sopt.app.common.fixtures.SoptampFixture.EDITED_STAMP_ACTIVITY_DATE;
import static org.sopt.app.common.fixtures.SoptampFixture.EDITED_STAMP_CONTENTS;
import static org.sopt.app.common.fixtures.SoptampFixture.EDITED_STAMP_IMAGE;
import static org.sopt.app.common.fixtures.SoptampFixture.getStampWithUserId;
import static org.sopt.app.common.fixtures.SoptampFixture.getStampWithUserIdAndMissionId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.stamp.StampDeletedEvent;
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

    @Test
    @DisplayName("SUCCESS_request에서 보낸 내용의 이미지와 내용을 기존 스탬프에 정상적으로 업데이트 함")
    void SUCCESS_editStampContents() {
        // given
        Long missionId = 100L;
        Long userId = 1L;

        StampRequest.EditStampRequest editStampRequest = SoptampFixture.getEditStampRequestWithMissionId(missionId);

        Stamp oldStamp = getStampWithUserIdAndMissionId(userId, missionId);
        Mockito.when(stampRepository.findByUserIdAndMissionId(userId, missionId)).thenReturn(Optional.of(oldStamp));

        //when
        StampInfo.Stamp result = stampService.editStampContents(editStampRequest, userId);

        //then
        assertThat(result.getId()).isEqualTo(oldStamp.getId());
        assertThat(oldStamp)
            .extracting(Stamp::getMissionId, Stamp::getUserId, Stamp::getContents, Stamp::getImages, Stamp::getActivityDate)
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

        Stamp oldStamp = getStampWithUserIdAndMissionId(userId, missionId);
        String oldContents = oldStamp.getContents();
        Mockito.when(stampRepository.findByUserIdAndMissionId(userId, missionId)).thenReturn(Optional.of(oldStamp));

        //when
        StampInfo.Stamp result = stampService.editStampContents(editStampRequest, userId);

        //then
        assertThat(result.getId()).isEqualTo(oldStamp.getId());
        assertThat(oldStamp)
            .extracting(Stamp::getMissionId, Stamp::getUserId, Stamp::getContents, Stamp::getImages, Stamp::getActivityDate)
            .contains(missionId, userId, oldContents, List.of(EDITED_STAMP_IMAGE), EDITED_STAMP_ACTIVITY_DATE);
    }

    @Test
    @DisplayName("SUCCESS_request의 image가 빈 문자열이면 image를 변경하지 않음")
    void SUCCESS_editStampContents_whenNoImage() {
        // given
        final Long userId = 1L;
        final Long missionId = 100L;
        StampRequest.EditStampRequest editStampRequest =
            new EditStampRequest(missionId, "", EDITED_STAMP_CONTENTS, EDITED_STAMP_ACTIVITY_DATE);

        Stamp oldStamp = getStampWithUserIdAndMissionId(userId, missionId);
        List<String> oldImages = oldStamp.getImages();
        Mockito.when(stampRepository.findByUserIdAndMissionId(userId, missionId)).thenReturn(Optional.of(oldStamp));

        //when
        StampInfo.Stamp result = stampService.editStampContents(editStampRequest, userId);

        //then
        assertThat(result.getId()).isEqualTo(oldStamp.getId());
        System.out.println(oldStamp.getImages());
        assertThat(oldStamp)
            .extracting(Stamp::getMissionId, Stamp::getUserId, Stamp::getContents, Stamp::getImages, Stamp::getActivityDate)
            .contains(missionId, userId, EDITED_STAMP_CONTENTS, oldImages, EDITED_STAMP_ACTIVITY_DATE);
    }

    @Test
    @DisplayName("FAIL_스탬프를 찾지 못하면 BadRequestException 발생")
    void FAIL_editStampContents_whenStampNotFound() {
        //given
        final Long userId = -1L;
        final Long missionId = -100L;
        final StampRequest.EditStampRequest editStampRequest = SoptampFixture.getEditStampRequestWithMissionId(missionId);

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
                .thenReturn(Optional.of(Stamp.builder().build()));

        //then
        assertThatThrownBy(() -> stampService.checkDuplicateStamp(userId, missionId))
            .isInstanceOf(BadRequestException.class)
            .satisfies(e -> {
               BadRequestException badRequestException = (BadRequestException) e;
               assertThat(badRequestException.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_STAMP);
            });
    }

    @Test
    @DisplayName("SUCCESS_스탬프를 찾으면 삭제")
    void SUCCESS_deleteStampById() {
        //given
        final Long stampId = 1L;
        final Stamp stamp = getStampWithUserId(stampId);

        List<String> images = stamp.getImages();

        Mockito.when(stampRepository.findById(stampId)).thenReturn(Optional.of(stamp));

        //when
        stampService.deleteStampById(stampId);

        //then
        verify(stampRepository).deleteById(stampId);

        ArgumentCaptor<StampDeletedEvent> stampDeletedEventCaptor = ArgumentCaptor.forClass(StampDeletedEvent.class);
        verify(eventPublisher).raise(stampDeletedEventCaptor.capture());
        StampDeletedEvent capturedEvent = stampDeletedEventCaptor.getValue();
        List<String> deletedImages = capturedEvent.getFileUrls();

        assertThat(deletedImages).isEqualTo(images);
    }

    @Test
    @DisplayName("FAIL_스탬프를 찾지 못하면 BadRequestException 발생")
    void FAIL_deleteStampById_whenStampNotFound() {
        //given
        final Long stampId = -1L;

        //when
        Mockito.when(stampRepository.findById(anyLong())).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> stampService.deleteStampById(stampId))
            .isInstanceOf(BadRequestException.class)
            .satisfies(e ->{
                BadRequestException exception =  (BadRequestException) e;
                assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.STAMP_NOT_FOUND);
            });
    }

    @Test
    @DisplayName("SUCCESS_모든 스탬프 삭제 시 스탬프 삭제 이벤트 발생")
    void SUCCESS_deleteAllStamps(){
        // given
        Long userId = 1L;

        List<String> userImages1 = List.of("image1", "image2");
        List<String> userImages2 = List.of("image3", "image4");

        List<String> allUserImages = new ArrayList<>(userImages1);
        allUserImages.addAll(userImages2);

        Stamp stamp1 = Stamp.builder()
            .userId(userId)
            .missionId(10L)
            .images(userImages1)
            .build();
        Stamp stamp2 = Stamp.builder()
            .userId(userId)
            .missionId(20L)
            .images(userImages2)
            .build();

        Mockito.when(stampRepository.findAllByUserId(userId)).thenReturn(List.of(stamp1, stamp2));

        // when
        stampService.deleteAllStamps(userId);

        // then
        verify(stampRepository).deleteAllByUserId(userId);

        ArgumentCaptor<StampDeletedEvent> stampDeletedEventCaptor = ArgumentCaptor.forClass(StampDeletedEvent.class);
        verify(eventPublisher).raise(stampDeletedEventCaptor.capture());
        StampDeletedEvent capturedEvent = stampDeletedEventCaptor.getValue();
        List<String> deletedImages = capturedEvent.getFileUrls();

        assertThat(deletedImages).isEqualTo(allUserImages);
    }

    @Test
    @DisplayName("SUCCESS_스탬프 아이디로 미션 아이디 조회")
    void SUCCESS_getMissionIdByStampId() {
        //given
        final Long stampId = 10L;
        final Long missionId = 20L;

        Stamp stamp = Stamp.builder()
            .id(stampId)
            .missionId(missionId)
            .build();

        Mockito.when(stampRepository.findById(stamp.getId())).thenReturn(Optional.of(stamp));

        //when
        Long result = stampService.getMissionIdByStampId(stampId);

        //then
        assertThat(result).isEqualTo(missionId);
    }

    @Test
    @DisplayName("FAIL_스탬프 아이디로 미션 아이디 조회되지 않을 시 BadRequestException 발생")
    void FAIL_getMissionIdByStampId_whenStampNotFound() {
        //given
        final Long anyStampId = -1L;

        //when
        Mockito.when(stampRepository.findById(anyLong())).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> stampService.getMissionIdByStampId(anyStampId))
            .isInstanceOf(BadRequestException.class)
            .satisfies(e -> {
                BadRequestException exception = (BadRequestException) e;
                assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.STAMP_NOT_FOUND);
            });
    }

    private Stamp getSavedStamp(Long userId, Long missionId) {
        final Optional<Stamp> savedStamp = Optional.of(
            getStampWithUserIdAndMissionId(userId, missionId));

        Mockito.when(stampRepository.findByUserIdAndMissionId(userId, missionId)).thenReturn(savedStamp);

        return savedStamp.get();
    }

}