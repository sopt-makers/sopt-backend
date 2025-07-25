package org.sopt.app.application.platform;

import static org.sopt.app.application.playground.PlaygroundHeaderCreator.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import org.sopt.app.application.auth.dto.PlaygroundAuthTokenInfo;
import org.sopt.app.application.platform.dto.PlatformUserInfoResponse;
import org.sopt.app.application.platform.dto.PlatformUserInfoWrapper;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.exception.UnauthorizedException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.enums.UserStatus;
import org.sopt.app.presentation.auth.AppAuthRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlatformService {

    private final PlatformClient platformClient;

    @Value("${external.auth.api-key}")
    private String apiKey;

    @Value("${external.auth.service-name}")
    private String serviceName;

    @Value("${sopt.current.generation}")
    private Long currentGeneration;

    public PlatformUserInfoResponse getPlatformUserInfoResponse(Long userId) {
        final Map<String, String> headers = createAuthorizationHeader();
        final Map<String, Collection<String>> params = createQueryParams(Collections.singletonList(userId));
        PlatformUserInfoWrapper platformUserInfoWrapper = platformClient.getPlatformUserInfo(headers, params);
        return platformUserInfoWrapper.data().getFirst();
    }

    public List<PlatformUserInfoResponse> getPlatformUserInfosResponse(List<Long> userIds) {
        final Map<String, String> headers = createAuthorizationHeader();
        final Map<String, Collection<String>> params = createQueryParams(userIds);
        PlatformUserInfoWrapper platformUserInfoWrapper = platformClient.getPlatformUserInfo(headers, params);
        return platformUserInfoWrapper.data();
    }

    public UserStatus getStatus(Long userId) {
        return Long.valueOf(getPlatformUserInfoResponse(userId).lastGeneration()).equals(currentGeneration) ? UserStatus.ACTIVE : UserStatus.INACTIVE;
    }

    private UserStatus getStatus(List<Long> generationList) {
        return generationList.contains(currentGeneration) ? UserStatus.ACTIVE : UserStatus.INACTIVE;
    }

    private Map<String, String> createAuthorizationHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put("x-api-key", apiKey);
        headers.put("x-service-name", serviceName);
        return headers;
    }

    private Map<String, Collection<String>> createQueryParams(List<Long> userId) {
        Map<String, Collection<String>> queryParams = new HashMap<>();
        for (Long id : userId) {
            queryParams.put("userIds", Collections.singletonList(id.toString()));
        }
        return queryParams;
    }

    public List<Long> getMemberGenerationList(Long userId) {
        return getPlatformUserInfoResponse(userId)
            .soptActivities().stream()
            .map(PlatformUserInfoResponse.SoptActivities::generation)
            .map(Integer::longValue)
            .distinct()
            .sorted(Comparator.reverseOrder())
            .toList();
    }

    public boolean isCurrentGeneration(Long generation) {
        return generation.equals(currentGeneration);
    }

    public PlaygroundProfileInfo.UserActiveInfo getUserActiveInfo(Long userId) {
        return new PlaygroundProfileInfo.UserActiveInfo(currentGeneration, getStatus(userId));
    }
}
