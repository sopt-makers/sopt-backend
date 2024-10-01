package org.sopt.app.common.response;

import com.slack.api.model.Attachment;
import java.util.List;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
public class SlackMessageGenerator {

    public static List<Attachment> generate(ExceptionWrapper exceptionWrapper, Long userId,String method, String requestUrl) {
        // 예외 정보를 기반으로 Attachment 생성
        Attachment exceptionClassAttachment = Attachment.builder()
                .color("#FF0000") // 색상 설정 (빨간색)
                .text("[ Exception Class ]: " + exceptionWrapper.getExceptionClassName())
                .build();
        Attachment exceptionUser = Attachment.builder()
                .color("#FF0000")
                .text("[ 유저 ID ]: " + userId)
                .build();
        Attachment exceptionMethodAttachment = Attachment.builder()
                .color("#FF0000")
                .text("[ Exception Method ]: " + exceptionWrapper.getExceptionMethodName())
                .build();
        Attachment exceptionLineNumberAttachment = Attachment.builder()
                .color("#FF0000")
                .text("[ Exception Line Number ]: " + exceptionWrapper.getExceptionLineNumber())
                .build();
        Attachment exceptionMessageAttachment = Attachment.builder()
                .color("#FF0000")
                .text("[ Exception Message ] : " + exceptionWrapper.getMessage())
                .build();
        Attachment excpetionMethodType = Attachment.builder()
                .color("#FF0000")
                .text("[ 메소드 타입 ] : " +method)
                .build();
        Attachment exceptionUrl = Attachment.builder()
                .color("#FF0000")
                .text("[ 요청 Url ] : " +requestUrl)
                .build();

        return java.util.List.of(
                exceptionClassAttachment,
                exceptionUser,
                exceptionMethodAttachment,
                exceptionLineNumberAttachment,
                exceptionMessageAttachment,
                excpetionMethodType,
                exceptionUrl
        );
    }
}
