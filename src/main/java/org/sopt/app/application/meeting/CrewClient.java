package org.sopt.app.application.meeting;

import feign.*;
import java.util.Map;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
public interface CrewClient {

    @RequestLine("GET /internal/meetings?orgId={playgroundId}&page={page}&take={take}&category={category}&isOnlyActiveGeneration={isOnlyActiveGeneration}")
    CrewMeetingResponse getAllMeetings(@HeaderMap Map<String, String> headers,
            @Param("playgroundId") Long playgroundId,
            @Param("page") int page,
            @Param("take") int take,
            @Param("category") String category,
            @Param("isOnlyActiveGeneration") Boolean isOnlyActiveGeneration
    );
}
