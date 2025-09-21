package org.sopt.app.application.platform;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.sopt.app.application.platform.dto.PlatformUserIdsRequest;
import org.sopt.app.application.platform.dto.PlatformUserInfoResponse;
import org.sopt.app.application.platform.dto.PlatformUserInfoWrapper;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.enums.UserStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

    // URL 길이 한도
    private static final int URL_QUERY_LENGTH_THRESHOLD = 1200;

    public PlatformUserInfoResponse getPlatformUserInfoResponse(Long userId) {
        final Map<String, String> headers = createAuthorizationHeader();
        final Map<String, String> params = createQueryParams(Collections.singletonList(userId));
        PlatformUserInfoWrapper platformUserInfoWrapper = platformClient.getPlatformUserInfo(headers, params);
        List<PlatformUserInfoResponse> data= platformUserInfoWrapper.data();
        if (data == null || data.isEmpty()) {
            throw new BadRequestException(ErrorCode.PLATFORM_USER_NOT_EXISTS);
        }
        return data.getFirst();
    }

    public List<PlatformUserInfoResponse> getPlatformUserInfosResponse(List<Long> userIds) {
        final Map<String, String> headers = createAuthorizationHeader();

        // 중복 제거
        userIds = userIds.stream().distinct().toList();
        final Map<String, String> params = createQueryParams(userIds);

        PlatformUserInfoWrapper platformUserInfoWrapper = platformClient.getPlatformUserInfo(headers, params);

        List<PlatformUserInfoResponse> data= platformUserInfoWrapper.data();
        if (data == null || data.isEmpty()) {
            throw new BadRequestException(ErrorCode.PLATFORM_USER_NOT_EXISTS);
        }
        return data;
    }

    // 길이에 따라 GET/POST 자동 선택
    public List<PlatformUserInfoResponse> getPlatformUserInfosResponseSmart(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER);
        }
        final Map<String, String> headers = createAuthorizationHeader();

        // 중복 제거
        userIds = userIds.stream().distinct().toList();

        // CSV 만들고 길이 체크
        final String csv = toCsv(userIds);
        final boolean usePost = csv.length() > URL_QUERY_LENGTH_THRESHOLD;

        PlatformUserInfoWrapper wrapper;
        if (usePost) {
            // POST with JSON body
            wrapper = platformClient.postPlatformUserInfo(headers, new PlatformUserIdsRequest(userIds));
        } else {
            // GET with CSV query
            final Map<String, String> params = Map.of("userIds", csv);
            wrapper = platformClient.getPlatformUserInfo(headers, params);
        }

        List<PlatformUserInfoResponse> data = wrapper.data();
        if (data == null || data.isEmpty()) {
            throw new BadRequestException(ErrorCode.PLATFORM_USER_NOT_EXISTS);
        }
        return data;
    }

    public UserStatus getStatus(Long userId) {
        return Long.valueOf(getPlatformUserInfoResponse(userId).lastGeneration()).equals(currentGeneration) ? UserStatus.ACTIVE : UserStatus.INACTIVE;
    }

    private UserStatus getStatus(List<Long> generationList) {
        return generationList.contains(currentGeneration) ? UserStatus.ACTIVE : UserStatus.INACTIVE;
    }

    private Map<String, String> createAuthorizationHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Api-Key", apiKey);
        headers.put("X-Service-Name", serviceName);
        return headers;
    }

    private Map<String, String> createQueryParams(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER);
        }
        final String csv = toCsv(userIds);
        return Collections.singletonMap("userIds", csv);
    }

    private String toCsv(List<Long> userIds) {
        return userIds.stream().map(String::valueOf).collect(Collectors.joining(","));
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
