package org.sopt.app.application.appservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.common.config.OperationConfig;
import org.sopt.app.common.config.OperationConfigCategory;
import org.sopt.app.common.exception.NotFoundException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.interfaces.postgres.OperationConfigRepository;

@ExtendWith(MockitoExtension.class)
class OperationConfigServiceTest {

    @Mock
    private OperationConfigRepository operationConfigRepository;

    @InjectMocks
    private OperationConfigService operationConfigService;

    @Test
    @DisplayName("SUCCESS_운영 설정 카테고리와 key로 value를 조회한다")
    void SUCCESS_getOperationConfigValue() {
        // given
        final String key = "reportFormUrl";
        final String value = "https://example.com/sopt-letter-report";
        OperationConfig operationConfig = OperationConfig.of(
                OperationConfigCategory.REVIEW_FORM,
                key,
                value,
                "솝레터 익명 신고 폼 URL"
        );
        when(operationConfigRepository.findByOperationConfigCategoryAndKey(OperationConfigCategory.REVIEW_FORM, key))
                .thenReturn(Optional.of(operationConfig));

        // when
        String result = operationConfigService.getOperationConfigValue(OperationConfigCategory.REVIEW_FORM, key);

        // then
        assertThat(result).isEqualTo(value);
        verify(operationConfigRepository, times(1))
                .findByOperationConfigCategoryAndKey(OperationConfigCategory.REVIEW_FORM, key);
    }

    @Test
    @DisplayName("FAIL_운영 설정이 존재하지 않으면 NotFoundException이 발생한다")
    void FAIL_getOperationConfigValue_notFound() {
        // given
        final String key = "reportFormUrl";
        when(operationConfigRepository.findByOperationConfigCategoryAndKey(OperationConfigCategory.REVIEW_FORM, key))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> operationConfigService.getOperationConfigValue(OperationConfigCategory.REVIEW_FORM, key))
                .isInstanceOf(NotFoundException.class)
                .satisfies(e -> {
                    NotFoundException exception = (NotFoundException) e;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ENTITY_NOT_FOUND);
                });
    }
}
