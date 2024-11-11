package org.sopt.app.application.app_service.dto;

public record AppServiceBadgeInfo(
        boolean displayMessage,
        boolean displayAlarmBadge,
        String alarmBadge
) { }
