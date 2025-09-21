package org.sopt.app.application.platform;

import feign.HeaderMap;
import feign.Headers;
import feign.QueryMap;
import feign.RequestLine;

import org.sopt.app.application.platform.dto.PlatformUserIdsRequest;
import org.sopt.app.application.platform.dto.PlatformUserInfoWrapper;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.Map;

@EnableFeignClients
public interface PlatformClient {

    // 유저정보 GET (CSV 쿼리)
    @RequestLine("GET /api/v1/users")
    PlatformUserInfoWrapper getPlatformUserInfo(@HeaderMap final Map<String, String> headers,
                                                @QueryMap Map<String, String> queryMap);

    // 유저정보 조회 POST (JSON 바디)
    @RequestLine("POST /api/v1/users")
    @Headers("Content-Type: application/json")
    PlatformUserInfoWrapper postPlatformUserInfo(@HeaderMap Map<String, String> headers,
        PlatformUserIdsRequest body);
}
