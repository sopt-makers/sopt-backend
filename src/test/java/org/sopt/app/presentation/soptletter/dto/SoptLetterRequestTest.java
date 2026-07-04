package org.sopt.app.presentation.soptletter.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;

class SoptLetterRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @ParameterizedTest
    @ValueSource(strings = {"😀", "👨‍👩‍👧‍👦", "👍🏽", "🇰🇷", "é"})
    @DisplayName("작성과 수정 요청에서 확장 grapheme cluster는 화면상 한 글자로 계산한다")
    void SUCCESS_validateGraphemeLength(String grapheme) {
        var requests = createRequestsWithContent(grapheme.repeat(350));

        assertThat(requests).allSatisfy(request -> assertThat(validator.validate(request)).isEmpty());
    }

    @Test
    @DisplayName("작성과 수정 요청에서 화면상 글자 수가 350자를 초과하면 검증에 실패한다")
    void FAIL_validateCombinedEmojiLength_exceedsLimit() {
        var requests = createRequestsWithContent("👨‍👩‍👧‍👦".repeat(351));

        assertThat(requests).allSatisfy(request -> assertThat(validator.validate(request))
            .extracting(violation -> violation.getMessage())
            .containsExactly("메시지는 공백을 포함하여 1자 이상 350자 이하로 작성해야 합니다."));
    }

    @Test
    @DisplayName("작성과 수정 요청에서 공백만 입력하면 필수값 검증에 실패한다")
    void FAIL_validateContent_blank() {
        var requests = createRequestsWithContent(" ");

        assertThat(requests).allSatisfy(request -> assertThat(validator.validate(request))
            .extracting(violation -> violation.getMessage())
            .containsExactly("메시지 내용은 필수입니다."));
    }

    private List<Object> createRequestsWithContent(String content) {
        var writeRequest = new SoptLetterRequest.WriteMessageRequest();
        var updateRequest = new SoptLetterRequest.UpdateMessageRequest();
        ReflectionTestUtils.setField(writeRequest, "content", content);
        ReflectionTestUtils.setField(updateRequest, "content", content);
        return List.of(writeRequest, updateRequest);
    }
}
