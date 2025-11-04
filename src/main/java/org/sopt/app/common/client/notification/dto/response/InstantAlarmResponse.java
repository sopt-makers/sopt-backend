package org.sopt.app.common.client.notification.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record InstantAlarmResponse (
    @Schema(description = "알림 서버 Response Status", example = "200")
    Integer status,
    @Schema(description = "성공 여부", example = "true")
    Boolean success,
    @Schema(description = "알림 서버 Response Message", example = "토큰 해지 성공")
    String message
)implements AlarmResponse{

    public static InstantAlarmResponse empty(){
        return new InstantAlarmResponse(200, true, "empty target");
    }
}
