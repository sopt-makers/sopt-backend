package org.sopt.app.common.validation;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GraphemeSizeTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("확장 grapheme cluster를 기준으로 설정한 최소, 최대 글자 수를 검증한다")
    void validateGraphemeSize() {
        assertThat(validator.validate(new TestValue("👨‍👩‍👧‍👦"))).isNotEmpty();
        assertThat(validator.validate(new TestValue("👨‍👩‍👧‍👦👍🏽"))).isEmpty();
        assertThat(validator.validate(new TestValue("👨‍👩‍👧‍👦👍🏽🇰🇷"))).isNotEmpty();
    }

    private record TestValue(
        @GraphemeSize(min = 2, max = 2)
        String value
    ) {
    }
}
