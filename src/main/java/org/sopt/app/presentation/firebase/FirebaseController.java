package org.sopt.app.presentation.firebase;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/firebase")
public class FirebaseController {

    @Operation(summary = "firebase 연동을 위한 정보 GET")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping(value = "")
    public FirebaseResponse.Main getFirebaseInfo() {

        return FirebaseResponse.Main.builder()
                .iosForceUpdateVersion("1.0.0")
                .iosAppVersion("1.0.2")
                .androidForceUpdateVersion("1.0.0")
                .androidAppVersion("1.0.0")
                .notice("안녕하세요, makers입니다. \n 현재 미션 수정/등록이 불가능한 이슈가 확인되어 원인 파악 중에 있습니다. \n 앱 이용에 불편을 드린 점 죄송합니다. \n 빠른 시일 내 복구 후 재공지 드리겠습니다.")
                .imgUrl(null)
                .build();
    }
}
