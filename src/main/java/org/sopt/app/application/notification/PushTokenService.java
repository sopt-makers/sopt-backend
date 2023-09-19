package org.sopt.app.application.notification;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.PushToken;
import org.sopt.app.domain.entity.PushTokenPK;
import org.sopt.app.domain.entity.User;
import org.sopt.app.interfaces.postgres.PushTokenRepository;
import org.sopt.app.presentation.notification.PushTokenRequest;
import org.sopt.app.presentation.notification.PushTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
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

    @Value("${makers.push.x-api-key}")
    private String apiKey;


    @Transactional(readOnly = true)
    public PushToken getDeviceTokenFromLocal(PushTokenPK pushTokenKey) {
        return pushTokenRepository.findById(pushTokenKey)
                .orElseThrow(() -> new BadRequestException(ErrorCode.PUSH_TOKEN_NOT_FOUND_FROM_EXTERNAL.getMessage()));
    }

    /**
     * 해당 기능은 알림 서버 내 조회 API 가 생성된 이후에 구현합니다.
     */
    /*
    public PushToken getDeviceTokenFromExternal(PushTokenPK pushTokenKey) {
    }
     */

    @Transactional(rollbackFor = BadRequestException.class)
    // 추후 비회원일 경우, Controller 단에서 고정 값으로 0과 같은 비회원 식별 번호 넣어줘야 함.
    public PushTokenResponse.StatusResponse registerDeviceToken(PushToken pushToken, String platform) {
        if (pushTokenRepository.existsById(PushTokenPK.of(pushToken.getPlaygroundId(), pushToken.getToken()))){
            // 아직 유효한 푸시 토큰을 그대로 쓰는 상황이라면 굳이 외부 서버 통신할 필요 없음
            return PushTokenResponse.StatusResponse.builder()
                    .status(200)
                    .success(true)
                    .message("already Registered")
                    .build();
        }
        try {
            val entity = new HttpEntity(
                    createBodyFor(pushToken),
                    createHeadersFor(ACTION_REGISTER, platform)
            );
            val response = sendRequestToPushServer(entity);
            pushTokenRepository.save(pushToken);
            return response.getBody();
        } catch (BadRequestException e) {
            return PushTokenResponse.StatusResponse.builder()
                    .status(e.getStatusCode().value())
                    .success(false)
                    .message(e.getResponseMessage())
                    .build();
        }
    }

    /**
     * 보류 메시지
     * => 이름은 업데이트지만 결국 새로 저장하는 것이기 때문에
     */
    /*
    @Transactional
    public PushTokenResponse.StatusResponse updateDeviceToken(PushToken targetPushToken, String newPushToken, String platform) {
        // 무조건 덮어쓰기
        try {
            targetPushToken.updatePushToken(newPushToken);
            val entity = new HttpEntity(
                    createBodyFor(targetPushToken),
                    createHeadersFor(ACTION_REGISTER, platform)
            );

            val response = sendRequestToPushServer(entity);
            return response.getBody();
        } catch (BadRequestException e) {
            return PushTokenResponse.StatusResponse.builder()
                    .status(e.getStatusCode().value())
                    .success(false)
                    .message(e.getResponseMessage())
                    .build();
        }
    }
    */
    @Transactional(rollbackFor = BadRequestException.class)
    public PushTokenResponse.StatusResponse deleteDeviceToken(PushToken pushToken, String platform) {
        if (!pushTokenRepository.existsById(PushTokenPK.of(pushToken.getPlaygroundId(), pushToken.getToken()))){
            // 내부 로컬 DB에서 이미 없다면 외부 서버 통신할 필요 없음
            return PushTokenResponse.StatusResponse.builder()
                    .status(200)
                    .success(true)
                    .message("already Deleted")
                    .build();
        }
        try {
            val entity = new HttpEntity(
                    createBodyFor(pushToken),
                    createHeadersFor(ACTION_DELETE, platform)
            );
            val response = sendRequestToPushServer(entity);
            pushTokenRepository.delete(pushToken);
            return response.getBody();
        } catch (BadRequestException e) {
            return PushTokenResponse.StatusResponse.builder()
                    .status(e.getStatusCode().value())
                    .success(false)
                    .message(e.getResponseMessage())
                    .build();
        }
    }

    @Transactional
    public void deleteAllDeviceTokenOf(User user) {
        // 우선 서비스 DB에 있는 모든 토큰 지워버리기
        pushTokenRepository.deleteAllByPlaygroundId(user.getPlaygroundId());
        // TODO: 알림 서버 FCM Token 삭제 요청 :: pushToken Repo 에서 모든 유저 토큰 가져와서 반복문으로 알림서버 토큰 삭제 API 호출하기
        // 알림 TF 쪽에서 구현 완료되면 끝
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

    private PushTokenRequest.ExternalRequest createBodyFor(PushToken pushToken) {
        return PushTokenRequest.ExternalMemberRequest.builder()
                .userIds(List.of(String.valueOf(pushToken.getPlaygroundId())))
                .pushToken(pushToken.getToken())
                .build();
    }
    private ResponseEntity<PushTokenResponse.StatusResponse> sendRequestToPushServer(HttpEntity requestEntity) {
        return restTemplate.exchange(
                baseURI,
                HttpMethod.POST,
                requestEntity,
                PushTokenResponse.StatusResponse.class
        );
    }

    private void checkIsSuccess(ResponseEntity<PushTokenResponse.StatusResponse> response) throws BadRequestException {
        // Push Server 로부터 400 Response 받았을 때
        if (Objects.requireNonNull(response.getBody()).getStatus() == HttpStatus.BAD_REQUEST.value()) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST_BODY_FOR_PUSH_TOKEN.getMessage());
        }
        // Push Server 로부터 500 Response 받았을 때
        else if (Objects.requireNonNull(response.getBody()).getStatus() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            throw new BadRequestException(ErrorCode.INTERNAL_SERVER_ERROR_IN_PUSH_SERVER.getMessage());
        }
    }
}
