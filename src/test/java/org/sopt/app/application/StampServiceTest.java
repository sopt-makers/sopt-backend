package org.sopt.app.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;
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

@ExtendWith(MockitoExtension.class)
class StampServiceTest {

    @Mock
    private StampRepository stampRepository;

    @InjectMocks
    private StampService stampService;

    @Test
    @DisplayName("200 - findStamp Success")
    void findStampSuccess() {
        //given
        final Long stampUserId = 1L;
        final Long stampMissionId = 1L;
        Stamp stamp = Stamp.builder().id(any(Long.class)).userId(stampUserId).missionId(stampMissionId).build();
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
        StampInfo.Stamp actual = stampService.findStamp(stampUserId, stampMissionId);
        //then
        assertThat(actual).isInstanceOf(StampInfo.Stamp.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("400 - 스탬프를 찾지 못하면 BadRequestException을 던진다.")
    void findStampBadRequest() {
        //when
        Mockito.when(stampRepository.findByUserIdAndMissionId(any(Long.class), any(Long.class)))
                .thenReturn(Optional.empty());
        //then
        assertThrows(BadRequestException.class, () -> {
            stampService.findStamp(any(Long.class), any(Long.class));
        });
    }

/* TODO: Implement the following tests

    @Test
    void uploadStampDeprecated() {
        // given

        final Long missionId = 1L;

        List<String> imgPaths = new ArrayList<>("requestImage1", "requestImage2");
        final Long requestUserId = 1L;

        final String requestContents = "requestContents";
        final String requestImage = "requestImage";
        final Long requestMissionId = 1L;
        RegisterStampRequest stampRequest = new RegisterStampRequest();
        stampRequest.setContents(requestContents);
        stampRequest.setImage(requestImage);
        stampRequest.setMissionId(requestMissionId);

        //when
        val imgList = new ArrayList<>(imgPaths);
        val stamp = this.convertStampImgDeprecated(stampRequest, imgList, requestUserId, requestMissionId);
        val newStamp = stampRepository.save(stamp);
        return StampInfo.Stamp.builder()
                .id(newStamp.getId())
                .contents(newStamp.getContents())
                .images(newStamp.getImages())
                .createdAt(newStamp.getCreatedAt())
                .updatedAt(newStamp.getUpdatedAt())
                .build();

        //then


    }
    @Test
    void uploadStamp() {
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