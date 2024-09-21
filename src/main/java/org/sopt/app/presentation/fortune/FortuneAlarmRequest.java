package org.sopt.app.presentation.fortune;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
public class FortuneAlarmRequest {

    @Schema(description = "유저 아이디", example = "[1]")
    @NotNull(message = "target user Id may not be null")
    private List<String> userIds;

    @Schema(description = "알림 제목")
    @NotNull(message = "Alarm Title may not be null")
    private String title;

    @Schema(description = "알림 내용")
    @NotNull(message = "Alarm Content may not be null")
    private String content;

    @Schema(description = "알림 카테고리")
    @NotNull(message = "Alarm Category may not be null")
    private String category;

    @Schema(description = "오늘의 솝마디 알림 딥링크")
    private String deepLink;

    public static FortuneAlarmRequest of(List<String> userIds, String title, String content,
                                         String category, String deepLink) {
        return FortuneAlarmRequest.builder()
                .category(category)
                .content(content)
                .deepLink(deepLink)
                .title(title)
                .userIds(userIds)
                .build();
    }
}
