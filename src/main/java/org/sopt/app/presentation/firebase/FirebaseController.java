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
                .iosForceUpdateVersion("2.1.0")
                .iosAppVersion("2.1.2")
                .androidForceUpdateVersion("1.0.0")
                .androidAppVersion("1.0.0")
                .notice("안녕하세요 32기 여러분들! 드디어 솝트 공식앱 안드로이드/iOS 공식 출시를 했습니다!\n"
                        + "iOS의 경우에는 기존의 \"솝탬프\" 앱을 업데이트 해주셔야 하고,\n"
                        + "안드로이드의 경우에는 \"SOPT\" 공식 앱을 구글플레이에서 받아주셔야 합니다.\n"
                        + "1차 행사때부터 활용 예정이라, **모든 분들이 반드시 솝트 앱을 미리 설치**해주시면 감사하겠습니다.\n"
                        + "(현재 솝트 앱 접속 후, 솝탬프에서 다른 분들의 솝탬프가 보이지 않는 이슈가 있습니다. 해당 부분은 빠르게 업데이트 예정입니다)\n"
                        + "또한, 정상적으로 활용하기 위해서는 플레이그라운드 회원 가입이 되어야합니다!\n"
                        + "혹시 아직 회원 가입이 되지 않았다면, 회원가입 후 플레이그라운드 프로필도 만들어주시길 부탁드리겠습니다.\n"
                        + "이용에 혹시 이슈가 있다면, 김나연(010-4519-0532)에게 연락주시면 감사하겠습니다")
                .imgUrl(null)
                .build();
    }
}
