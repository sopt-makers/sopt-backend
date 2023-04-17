package org.sopt.app.v1.presentation.firebase;


import lombok.RequiredArgsConstructor;
import org.sopt.app.v1.presentation.firebase.dto.FirebaseResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FirebaseControllerV1 {


    /**
     * firebase 연동을 위한 정보 GET
     *
     * @return
     */
    @GetMapping(value = "/api/v1/firebase")
    public FirebaseResponseDto getfirebaseInfo() {

        return FirebaseResponseDto.builder()
                .iosForceUpdateVersion("1.0.0")
                .iosAppVersion("1.0.2")
                .androidForceUpdateVersion("1.0.0")
                .androidAppVersion("1.0.0")
                .notice("안녕하세요, makers입니다. \n 현재 미션 수정/등록이 불가능한 이슈가 확인되어 원인 파악 중에 있습니다. \n 앱 이용에 불편을 드린 점 죄송합니다. \n 빠른 시일 내 복구 후 재공지 드리겠습니다.")
                .imgUrl(null)
                .build();
    }
}
