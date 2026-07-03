package org.sopt.app.presentation.soptletter.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.sopt.app.common.validation.GraphemeSize;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SoptLetterRequest {

    @Getter
    @ToString
    public static class WriteMessageRequest {
        @NotBlank(message = "메시지 내용은 필수입니다.")
        @GraphemeSize(max = 350, message = "메시지는 공백을 포함하여 1자 이상 350자 이하로 작성해야 합니다.")
        private String content;
    }

    @Getter
    @ToString
    public static class UpdateMessageRequest {
        @NotBlank(message = "메시지 내용은 필수입니다.")
        @GraphemeSize(max = 350, message = "메시지는 공백을 포함하여 1자 이상 350자 이하로 작성해야 합니다.")
        private String content;
    }
}
