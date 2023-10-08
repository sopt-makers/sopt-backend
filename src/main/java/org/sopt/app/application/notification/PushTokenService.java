package org.sopt.app.application.notification;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.PushToken;
import org.sopt.app.domain.entity.User;
import org.sopt.app.domain.enums.PushTokenPlatform;
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

    private final PushTokenRepository pushTokenRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${makers.push.server}")
    private String baseURI;

    @Value("${makers.push.x-api-key}")
    private String apiKey;


    @Transactional(readOnly = true)
    public PushToken getDeviceToken(Long userId, String token) {
        return pushTokenRepository.findByUserIdAndToken(userId, token)
                .orElseThrow(() -> new BadRequestException(ErrorCode.PUSH_TOKEN_NOT_FOUND_FROM_LOCAL.getMessage()));
    }


    @Transactional(rollbackFor = BadRequestException.class)
    // 추후 비회원일 경우, Controller 단에서 고정 값으로 0과 같은 비회원 식별 번호 넣어줘야 함.
    public PushTokenResponse.StatusResponse registerDeviceToken(User user, String pushToken, String platform) {
        if (!pushTokenRepository.existsByUserIdAndToken(user.getId(), pushToken)) {
            PushToken registerToken = PushToken.builder()
                    .userId(user.getId())
                    .playgroundId(user.getPlaygroundId())
                    .token(pushToken)
                    .platform(PushTokenPlatform.valueOf(platform))
                    .build();
            try {
                val entity = new HttpEntity(
                        createBodyFor(registerToken),
                        createHeadersFor(ACTION_REGISTER, platform)
                );
                val response = sendRequestToPushServer(entity);
                pushTokenRepository.save(registerToken);
                return response.getBody();
            } catch (BadRequestException e) {
                return PushTokenResponse.StatusResponse.builder()
                        .status(e.getStatusCode().value())
                        .success(false)
                        .message(e.getResponseMessage())
                        .build();
            }
        }
        return PushTokenResponse.StatusResponse.builder()
                .status(HttpStatus.OK.value())
                .success(true)
                .message("토큰 등록 성공")
                .build();
    }


    // 유효하지 않은 토큰으로 인해 BadRequest가 발생하더라도 넘어가야함.(Local 에는 모든 토큰을 쌓아놓기 때문에)
    @Transactional
    public PushTokenResponse.StatusResponse deleteDeviceToken(PushToken pushToken) {
        try {
            val entity = new HttpEntity(
                    createBodyFor(pushToken),
                    createHeadersFor(ACTION_DELETE, pushToken.getPlatform().name())
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
    public Integer deleteAllDeviceTokenOf(User user) {
        // 기존에 저장되어 있던 Tokens -> 알림 서버에 삭제 요청
        List<PushToken> userTokens = pushTokenRepository.findAllByUserId(user.getId());
        int failedCount = 0;
        for (PushToken token : userTokens) {
            PushTokenResponse.StatusResponse statusResponse = deleteDeviceToken(token);
            if (!statusResponse.getSuccess()) {
                failedCount += 1;
            }
        }
        // 우선 서비스 DB에 있는 모든 토큰 지워버리기
        pushTokenRepository.deleteAllByUserId(user.getId());
        return failedCount;
    }

    private HttpHeaders createHeadersFor(String action, String platform) {
        val headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
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
                .deviceToken(pushToken.getToken())
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
}
