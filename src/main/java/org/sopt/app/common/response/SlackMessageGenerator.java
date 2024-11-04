package org.sopt.app.common.response;

import com.slack.api.model.Attachment;
import com.slack.api.model.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Getter
@Service
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SlackMessageGenerator {

    public static List<Attachment> generate(ExceptionWrapper exceptionWrapper, Long userId, String method, String requestUrl) {
        String requestTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").format(LocalDateTime.now());
        Attachment attachment = Attachment.builder()
                .color("#FF0000")
                .title(requestTime + " 발생 에러 로그")
                .fields(List.of(
                        generateSlackField("[ Exception Class ]", exceptionWrapper.getExceptionClassName()),
                        generateSlackField("[ Exception Message ]", exceptionWrapper.getMessage())
                ))
                .build();
        Attachment attachmentOther = Attachment.builder()
                .color("#FFFF00")
                .title("로그 첨부사항")
                .fields(List.of(
                        generateSlackField("[ 유저 ID ]", String.valueOf(userId)),
                        generateSlackField("[ 메소드 타입 ]", method),
                        generateSlackField("[ 요청 URL ]", requestUrl),
                        generateSlackField("[ Exception Method ]", exceptionWrapper.getExceptionMethodName()),
                        generateSlackField("[ Exception Line Number ]", String.valueOf(exceptionWrapper.getExceptionLineNumber()))
                ))
                .build();
        return List.of(attachment,attachmentOther);
    }

    // Field 생성 메서드
    private static Field generateSlackField(String title, String value) {
        return Field.builder()
                .title(title)
                .value(value)
                .valueShortEnough(false)
                .build();
    }
}
