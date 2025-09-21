package org.sopt.app.common.response;

import static org.sopt.app.common.response.ExceptionWrapper.extractExceptionWrapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.sopt.app.application.slack.SlackService;
import org.sopt.app.domain.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class SlackLoggerAspect {

    private final HttpServletRequest request;
    private final SlackService slackService;

    // 가능한 모든 케이스에서 userId를 안전하게 추출
    private Long resolveUserIdSafely() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) return null;

            Object principal = authentication.getPrincipal();

            // 1) 애플리케이션에서 전역적으로 쓰는 Long
            if (principal instanceof Long l) return l;

            // 2) 도메인 User 엔티티
            if (principal instanceof User u) return u.getId();

            // 3) Spring Security UserDetails (username이 숫자 id일 수 있음)
            if (principal instanceof UserDetails ud) {
                return parseLongOrNull(ud.getUsername());
            }

            // 4) String (예: "anonymousUser" 또는 "123")
            if (principal instanceof String s) {
                Long parsed = parseLongOrNull(s);
                if (parsed != null) return parsed; // 숫자면 사용
                return null; // anonymous 등은 null 처리
            }

            // 5) JWT/기타 토큰: authentication.getName()이 종종 subject/username
            return parseLongOrNull(authentication.getName());
        } catch (Exception ex) {
            log.debug("SlackLoggerAspect: failed to resolve user id: {}", ex.getMessage());
            return null; // userId를 못 구해도 로깅은 계속 진행
        }
    }

    private Long parseLongOrNull(String v) {
        if (v == null) return null;
        try { return Long.parseLong(v); }
        catch (NumberFormatException e) { return null; }
    }

    @Before(value = "@annotation(org.sopt.app.common.response.SlackLogger)")
    public void sendLogForError(final JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();

            // 이 애스펙트는 보통 ControllerAdvice의 핸들러(예외 1개 파라미터)를 대상으로 사용된다고 가정
            if (args.length == 1 && args[0] instanceof Exception e) {
                String requestUrl = request.getRequestURI();
                String requestMethod = request.getMethod();

                ExceptionWrapper exceptionWrapper = extractExceptionWrapper(e);
                Long userId = resolveUserIdSafely();

                slackService.sendSlackMessage(
                    SlackMessageGenerator.generate(exceptionWrapper, userId, requestMethod, requestUrl)
                );
            } else {
                // 잘못 붙은 경우에도 전체 플로우를 깨지 않도록 경고만 남김
                log.warn("Slack Logger skipped: invalid usage (expects single Exception arg). method={}, args={}",
                    joinPoint.getSignature(), args.length);
            }
        } catch (Exception ex) {
            // 로거 자체가 장애 유발하지 않도록 방어
            log.error("Slack Logger failed: {}", ex.getMessage(), ex);
        }
    }
}