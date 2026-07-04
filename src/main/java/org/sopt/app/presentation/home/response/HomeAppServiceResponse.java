package org.sopt.app.presentation.home.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.sopt.app.application.appservice.dto.AppServiceEntryStatusResponse;

@Schema(description = "홈 앱 서비스 목록 응답")
public record HomeAppServiceResponse(
    @Schema(description = "앱잼 모드 여부", example = "true")
    boolean isAppjamMode,

    @Schema(description = "홈 앱 서비스 목록")
    List<AppServiceEntryStatusResponse> appServices
) {

    public static HomeAppServiceResponse of(
        boolean isAppjamMode,
        List<AppServiceEntryStatusResponse> appServices
    ) {
        return new HomeAppServiceResponse(isAppjamMode, appServices);
    }
}
