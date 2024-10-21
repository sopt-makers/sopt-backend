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
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class SlackLoggerAspect {

    private final HttpServletRequest request;
    private final SlackService slackService;

    private Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        val user = (User) authentication.getPrincipal();
        return user.getId();
    }

    @Before(value = "@annotation(org.sopt.app.common.response.SlackLogger)")
    public void sendLogForError(final JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length != 1) {
            log.warn("Slack Logger Failed : Invalid Used");
            return;
        }

        if (args[0] instanceof Exception e) {
            String requestUrl = request.getRequestURI();
            String requestMethod = request.getMethod();
            ExceptionWrapper exceptionWrapper = extractExceptionWrapper(e);
            slackService.sendSlackMessage(
                    SlackMessageGenerator.generate(exceptionWrapper,getUserId(),requestMethod,requestUrl));
        }
    }
}
