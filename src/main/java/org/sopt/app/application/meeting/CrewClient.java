package org.sopt.app.application.meeting;

import feign.*;
import java.util.Map;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
public interface CrewClient {
    @RequestLine("GET /internal/meetings")
    CrewMeetingResponse getAllMeetings(@HeaderMap Map<String, String> headers,
            @Param("orgId") Long playgroundId,
            @Param("page") Long page,
            @Param("take") Long take,
            @Param("category") String category,
            @Param("isOnlyActiveGeneration") Boolean isOnlyActiveGeneration
    );
}
