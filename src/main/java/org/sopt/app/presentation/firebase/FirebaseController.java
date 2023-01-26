package org.sopt.app.presentation.firebase;


import lombok.RequiredArgsConstructor;
import org.sopt.app.presentation.firebase.dto.FirebaseResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FirebaseController {


  /**
   * firebase 연동을 위한 정보 GET
   * @return
   */
  @GetMapping(value = "/api/v1/firebase")
  public FirebaseResponseDto getfirebaseInfo() {

    return FirebaseResponseDto.builder()
        .iosForceUpdateVersion("1.0.3")
        .iosAppVersion("1.0.2")
        .androidForceUpdateVersion("1.0.0")
        .androidAppVersion("1.0.0")
        .notice("공지내용")
        .imgUrl(null)
        .build();
  }
}
