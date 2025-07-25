package org.sopt.app.application.platform;

import feign.HeaderMap;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;
import org.sopt.app.application.platform.dto.PlatformUserInfoResponse;
import org.sopt.app.application.platform.dto.PlatformUserInfoWrapper;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@EnableFeignClients
public interface PlatformClient {

    @RequestLine("GET /api/v1/users")
    PlatformUserInfoWrapper getPlatformUserInfo(@HeaderMap final Map<String, String> headers,
                                                @QueryMap Map<String, Collection<String>> queryMap);

}
