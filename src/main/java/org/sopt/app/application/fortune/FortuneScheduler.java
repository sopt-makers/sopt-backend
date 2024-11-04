package org.sopt.app.application.fortune;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.user.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FortuneScheduler {
    private final UserService userService;
    private final FortuneNotificationSender fortuneNotificationSender;

    @Scheduled(cron = "0 0 9 * * ?")
    public void runDailyFortuneCreation() {
        List<Long> playgroundIds = userService.getAllPlaygroundIds();
        fortuneNotificationSender.sendFortuneNotification(playgroundIds);
    }
}