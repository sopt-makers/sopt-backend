package org.sopt.app.domain.entity.soptamp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.springframework.util.StringUtils;

class StampTest {

    @Nested
    @DisplayName("Stamp 유효성 검사 테스트")
    class ValidateTest{

        private Stamp.StampBuilder getStampBuilder(){
            return  Stamp.builder()
                .missionId(1L)
                .contents("미션 완료~")
                .activityDate("2025-01-01")
                .images(List.of("test image"));
        }

        @Test
        @DisplayName("SUCCESS_ 정상적인 stamp 인 경우 예외가 발생하지 않음")
        void SUCCESS_validate(){
            // given
            Stamp stamp = getStampBuilder().build();
            
            // when & then
            assertDoesNotThrow(stamp::validate);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "", "\t"})
        @DisplayName("FAIL_stamp content가 공백이거나 null 인 경우 정상적으로 예외가 발생함")
        void FAIL_validate_whenContentBlankOrNull(String content){
            // given
            Stamp stamp = getStampBuilder()
                .contents(content)
                .build();

            // when & then
            assertThatThrownBy(stamp::validate)
                .isInstanceOf(BadRequestException.class)
                .satisfies(e -> {
                    BadRequestException exception = (BadRequestException) e;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_STAMP_CONTENTS);
                });
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("FAIL_stamp images 가 empty 거나 null 일 경우 정상적으로 예외가 발생함")
        void FAIL_validate_whenImagesNullOrBlank(List<String> images){
            // given
            Stamp stamp = getStampBuilder()
                .images(images)
                .build();

            // when & then
            assertThatThrownBy(stamp::validate)
                .isInstanceOf(BadRequestException.class)
                .satisfies(e -> {
                    BadRequestException exception = (BadRequestException) e;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_STAMP_IMAGES);
                });
        }

        @Test
        @DisplayName("FAIL_stamp activityDate 가 null 일 경우 정상적으로 예외가 발생함")
        void FAIL_validate_whenActivityDateNull(){
            // given
            Stamp stamp = getStampBuilder()
                .activityDate(null)
                .build();

            // when & then
            assertThatThrownBy(stamp::validate)
                .isInstanceOf(BadRequestException.class)
                .satisfies(e -> {
                    BadRequestException exception = (BadRequestException) e;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_STAMP_ACTIVITY_DATE);
                });
        }

        @Test
        @DisplayName("FAIL_stamp missionId 가 null 일 경우 정상적으로 예외가 발생함")
        void FAIL_validate_whenMissionIdNull(){
            // given
            Stamp stamp = getStampBuilder()
                .missionId(null)
                .build();

            // when & then
            assertThatThrownBy(stamp::validate)
                .isInstanceOf(BadRequestException.class)
                .satisfies(e -> {
                    BadRequestException exception = (BadRequestException) e;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_STAMP_MISSION_ID);
                });
        }

    }

}