package org.sopt.app.application.app_service;

import java.util.List;
import lombok.*;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AppServiceResponse {

    private final AppServiceName serviceName;
    private final boolean displayAlarmBadge;
    private final String alarmBadge;
    private final boolean displayMessage;
    private final List<String> messages;
    private final List<String> messageColors;
}
