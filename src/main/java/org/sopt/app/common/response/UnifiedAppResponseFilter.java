package org.sopt.app.common.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@RequiredArgsConstructor
public class UnifiedAppResponseFilter extends OncePerRequestFilter {

    private static final String APP_API_PREFIX = "/api/v2/";
    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        String method = request.getMethod();
        return !path.startsWith(APP_API_PREFIX)
            || matchesPath(path, "/api/v2/admin")
            || matchesPath(path, "/api/v2/health")
            || ("POST".equals(method) && matchesPath(path, "/api/v2/notification"))
            || "HEAD".equals(method)
            || "OPTIONS".equals(method);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        var wrapped = new AppResponseWrapper(response);
        chain.doFilter(request, wrapped);
        byte[] body = unifiedBody(wrapped);
        if (body != null) {
            wrapped.resetBuffer();
            wrapped.setContentType(MediaType.APPLICATION_JSON_VALUE);
            wrapped.setCharacterEncoding(StandardCharsets.UTF_8.name());
            wrapped.setContentLength(body.length);
            wrapped.getOutputStream().write(body);
        }
        wrapped.copyBodyToResponse();
    }

    private byte[] unifiedBody(ContentCachingResponseWrapper response) throws IOException {
        int status = response.getStatus();
        if (status < 200 || status == 204 || status == 205 || (status >= 300 && status < 400)
                || response.containsHeader(HttpHeaders.CONTENT_ENCODING)) {
            return null;
        }

        String contentType = response.getContentType();
        byte[] original = response.getContentAsByteArray();
        if (original.length > 0 && !isJson(contentType)) {
            return null;
        }

        JsonNode payload;
        try {
            payload = original.length == 0 ? objectMapper.nullNode() : objectMapper.readTree(original);
        } catch (JsonProcessingException e) {
            return null;
        }

        boolean success = status < 300;
        HttpStatus httpStatus = HttpStatus.resolve(status);
        String defaultMessage = httpStatus == null ? "" : httpStatus.getReasonPhrase();
        ObjectNode body = objectMapper.createObjectNode()
            .put("success", success)
            .put("message", success ? defaultMessage : payload.path("message").asText(defaultMessage));
        body.set("data", success ? payload : validationDetails(status, payload));
        return objectMapper.writeValueAsBytes(body);
    }

    private JsonNode validationDetails(int status, JsonNode payload) {
        if (status != 400 || !payload.path("errors").isArray()) {
            return objectMapper.nullNode();
        }
        ObjectNode details = objectMapper.createObjectNode();
        for (JsonNode error : payload.path("errors")) {
            String field = error.path("field").asText("");
            String reason = error.path("reason").asText("");
            if (!field.isBlank() && !"typeMismatch".equals(reason)) {
                details.put("valid_" + field, reason);
            }
        }
        return details.isEmpty() ? objectMapper.nullNode() : details;
    }

    private boolean matchesPath(String path, String prefix) {
        return path.equals(prefix) || path.startsWith(prefix + "/");
    }

    private boolean isJson(String contentType) {
        if (contentType == null) {
            return false;
        }
        MediaType type = MediaType.parseMediaType(contentType);
        return MediaType.APPLICATION_JSON.isCompatibleWith(type) || type.getSubtype().endsWith("+json");
    }

    // Keep servlet errors in the capture buffer so missing-token responses also use the same format.
    private static class AppResponseWrapper extends ContentCachingResponseWrapper {

        AppResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void sendError(int status) {
            resetBuffer();
            setStatus(status);
        }

        @Override
        public void sendError(int status, String message) {
            sendError(status);
        }
    }
}
