package org.sopt.app.common.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class UnifiedAppResponseFilterTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UnifiedAppResponseFilter filter = new UnifiedAppResponseFilter(objectMapper);

    @ParameterizedTest
    @ValueSource(strings = {"{\"id\":1}", "[{\"id\":1}]", "null"})
    void wrapsJsonWithoutChangingStatusOrData(String payload) throws Exception {
        var response = filter("GET", "/api/v2/home/app-service", json(200, payload));
        var body = objectMapper.readTree(response.getContentAsByteArray());

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(body.get("success").booleanValue()).isTrue();
        assertThat(body.get("data")).isEqualTo(objectMapper.readTree(payload));
    }

    @Test
    void wrapsEmptySuccessWithNullData() throws Exception {
        var response = filter("PATCH", "/api/v2/notification/read", (request, result) -> {});

        assertThat(objectMapper.readTree(response.getContentAsByteArray()))
            .isEqualTo(objectMapper.readTree("{\"success\":true,\"message\":\"OK\",\"data\":null}"));
    }

    @Test
    void convertsLegacyFieldErrorsToUnifiedValidationData() throws Exception {
        var response = filter("POST", "/api/v2/sopt-letter/topics/1/messages", json(400, """
            {
              "status": "BAD_REQUEST",
              "message": "요청을 확인해주세요",
              "errors": [{"field":"content","value":"","reason":"필수 값입니다"}]
            }
            """));

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(objectMapper.readTree(response.getContentAsByteArray()))
            .isEqualTo(objectMapper.readTree("""
                {"success":false,"message":"요청을 확인해주세요","data":{"valid_content":"필수 값입니다"}}
                """));
    }

    @Test
    void wrapsServletUnauthorizedResponseWithoutChangingStatus() throws Exception {
        var response = filter("GET", "/api/v2/user/generation",
            (request, result) -> ((HttpServletResponse) result).sendError(401, "Unauthorized"));

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(objectMapper.readTree(response.getContentAsByteArray()))
            .isEqualTo(objectMapper.readTree("{\"success\":false,\"message\":\"Unauthorized\",\"data\":null}"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/v2/health", "/api/v2/admin/notification", "/api/v2/notification", "/v3/api-docs"})
    void leavesNonAppResponsesAlone(String path) throws Exception {
        assertThat(filter("POST", path, json(200, "{}")).getContentAsString()).isEqualTo("{}");
    }

    @Test
    void leavesNonJsonAndNoContentResponsesAlone() throws Exception {
        var plain = filter("GET", "/api/v2/home/app-service", (request, result) -> {
            result.setContentType("text/plain");
            result.getWriter().write("plain");
        });
        var empty = filter("DELETE", "/api/v2/user/push-token",
            (request, result) -> ((HttpServletResponse) result).setStatus(204));

        assertThat(plain.getContentAsString()).isEqualTo("plain");
        assertThat(empty.getStatus()).isEqualTo(204);
        assertThat(empty.getContentAsByteArray()).isEmpty();
    }

    private MockHttpServletResponse filter(String method, String path, FilterChain chain) throws Exception {
        var response = new MockHttpServletResponse();
        filter.doFilter(new MockHttpServletRequest(method, path), response, chain);
        return response;
    }

    private FilterChain json(int status, String body) {
        return (request, result) -> {
            ((HttpServletResponse) result).setStatus(status);
            result.setContentType("application/json");
            result.setCharacterEncoding("UTF-8");
            result.getWriter().write(body);
        };
    }
}
