package org.sopt.app.application.notification;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.PushToken;
import org.sopt.app.domain.entity.PushTokenPK;
import org.sopt.app.domain.entity.User;
import org.sopt.app.interfaces.postgres.PushTokenRepository;
import org.sopt.app.presentation.notification.PushTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PushTokenService {

    private static final String ACTION_REGISTER = "register";
    private static final String ACTION_DELETE = "cancel";
    private static final String ACTION_SEND = "send";

    private final PushTokenRepository pushTokenRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${makers.push.server}")
    private String baseURI;

    @Value("${makers.playground.x-api-key}")
    private String apiKey;

    public PushTokenResponse.StatusResponse registerDeviceToken(PushToken pushToken, String platform) {
        if (pushTokenRepository.existsById(PushTokenPK.of(pushToken.getUserId(), pushToken.getToken()))){
            // 아직 유효한 푸시 토큰을 그대로 쓰는 상황이라면 굳이 외부 서버 통신할 필요 없음
            return PushTokenResponse.StatusResponse.builder()
                    .status(200)
                    .success(true)
                    .message("already Registered")
                    .build();
        }
        /*
        val headers = createHeadersFor(ACTION_REGISTER, platform);
        val entity = new HttpEntity(pushToken, headers);

        val response = restTemplate.exchange(
                baseURI,
                HttpMethod.POST,
                entity,
                PushTokenResponse.StatusResponse.class
        );
         */
        // Push Server 등록이 성공했을 때만 저장하기
//        if(isSuccess(response)) {
            pushTokenRepository.save(pushToken);
//        }
//        return response.getBody();
        return PushTokenResponse.StatusResponse.builder()
                .status(200)
                .success(true)
                .message("already Registered")
                .build();
    }

    public PushTokenResponse.StatusResponse deleteDeviceToken(PushToken pushToken, String platform) {
        val headers = createHeadersFor(ACTION_DELETE, platform);
        val entity = new HttpEntity(pushToken, headers);

        val response = restTemplate.exchange(
                baseURI,
                HttpMethod.POST,
                entity,
                PushTokenResponse.StatusResponse.class
        );
        isSuccess(response);
        return response.getBody();
    }

    @Transactional
    public void deleteAllDeviceTokenOf(User user) {
        // TODO: 알림 서버 FCM Token 삭제 요청 :: pushToken Repo 에서 모든 유저 토큰 가져와서 반복문으로 알림서버 토큰 삭제 API 호출하기
        pushTokenRepository.deleteAllByUserId(user.getId());
    }

    private HttpHeaders createHeadersFor(String action, String platform) {
        val headers = new HttpHeaders();
        headers.add("content-type", "application/json");
        headers.add("action", action);
        headers.add("x-api-key", apiKey);
        headers.add("service", "app");
        headers.add("transactionId", UUID.randomUUID().toString());
        if (Objects.nonNull(platform)) {
            headers.add("platform", platform);
        }
        return headers;
    }
    private boolean isSuccess(ResponseEntity<PushTokenResponse.StatusResponse> response) throws BadRequestException {
        // Push Server 로부터 400 Response 받았을 때
        if (Objects.requireNonNull(response.getBody()).getStatus() == HttpStatus.BAD_REQUEST.value()) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST_BODY_FOR_PUSH_TOKEN.getMessage());
        }
        // Push Server 로부터 500 Response 받았을 때
        else if (Objects.requireNonNull(response.getBody()).getStatus() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            throw new BadRequestException(ErrorCode.INTERNAL_SERVER_ERROR_IN_PUSH_SERVER.getMessage());
        }
        return true;
    }
}
