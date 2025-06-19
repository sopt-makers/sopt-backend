package org.sopt.app.application.platform;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.app.application.platform.dto.PlatformUserInfoResponse;
import org.sopt.app.application.platform.dto.PlatformUserInfoWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

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
}
