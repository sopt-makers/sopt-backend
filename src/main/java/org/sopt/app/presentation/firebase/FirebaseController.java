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
    @GetMapping
    public FirebaseResponse.Main getFirebaseInfo() {

        return FirebaseResponse.Main.builder()
                .iosForceUpdateVersion("2.6.3")
                .iosAppVersion("2.2.0")
                .androidForceUpdateVersion("1.0.0")
                .androidAppVersion("2.0.0")
                .notice("안녕하세요, Makers 입니다. SOPT APP이 더 편리한 서비스 경험을 위해 개선 되었어요 ‘◡’\n"
                    + "지금 바로 업데이트를 통해 더 편리하고, 안정적인 SOPT APP을 경험해보세요!\n"
                       )
                .imgUrl(null)
                .build();
    }
}
