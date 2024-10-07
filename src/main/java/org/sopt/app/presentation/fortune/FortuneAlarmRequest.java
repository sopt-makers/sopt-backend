package org.sopt.app.presentation.fortune;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.*;
import org.sopt.app.domain.enums.NotificationCategory;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PUBLIC)
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

    public static FortuneAlarmRequest of(List<Long> userIds){
        return FortuneAlarmRequest.builder()
                .userIds(userIds.stream().map(String::valueOf).toList())
                .title("오늘의 솝마디")
                .content("오늘의 솝마디를 확인해보세요!")
                .category(NotificationCategory.NEWS.name())
                .deepLink("home/fortune")
                .build();
    }
}
