package org.sopt.app.application.soptletter;

import java.util.Set;
import java.util.stream.Collectors;
import org.sopt.app.application.platform.dto.PlatformUserInfoResponse;
import org.sopt.app.application.soptamp.AuthUserProfileReader;
import org.sopt.app.common.client.notification.AlarmSender;
import org.sopt.app.common.client.notification.dto.request.InstantAlarmRequest;
import org.sopt.app.domain.enums.NotificationCategory;
import org.sopt.app.interfaces.postgres.soptletter.SoptLetterProfileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SoptLetterNotificationService {

    private final AuthUserProfileReader authUserProfileReader;
    private final SoptLetterProfileRepository soptLetterProfileRepository;
    private final AlarmSender<InstantAlarmRequest> instantAlarmSender;
    private final Long currentGeneration;

    public SoptLetterNotificationService(
        AuthUserProfileReader authUserProfileReader,
        SoptLetterProfileRepository soptLetterProfileRepository,
        AlarmSender<InstantAlarmRequest> instantAlarmSender,
        @Value("${sopt.current.generation}") Long currentGeneration
    ) {
        this.authUserProfileReader = authUserProfileReader;
        this.soptLetterProfileRepository = soptLetterProfileRepository;
        this.instantAlarmSender = instantAlarmSender;
        this.currentGeneration = currentGeneration;
    }

    public void sendUnwrittenReminder(String title, String content, String deepLink, String webLink) {
        Set<String> targetUserIds = getUnwrittenCurrentGenerationUserIds().stream()
            .map(String::valueOf)
            .collect(Collectors.toSet());

        if (targetUserIds.isEmpty()) {
            return;
        }

        InstantAlarmRequest request = InstantAlarmRequest
            .builderWithRequired(targetUserIds, title, content, NotificationCategory.NOTICE)
            .deepLink(deepLink)
            .webLink(webLink)
            .build();

        instantAlarmSender.send(request);
    }

    private Set<Long> getUnwrittenCurrentGenerationUserIds() {
        Set<Long> currentGenerationUserIds = authUserProfileReader.getSoptUserProfilesByGeneration(currentGeneration)
            .stream()
            .map(PlatformUserInfoResponse::userId)
            .map(userId -> Long.valueOf(userId))
            .collect(Collectors.toSet());

        if (currentGenerationUserIds.isEmpty()) {
            return Set.of();
        }

        Set<Long> writtenUserIds = soptLetterProfileRepository.findWrittenUserIdsByUserIdIn(currentGenerationUserIds);
        currentGenerationUserIds.removeAll(writtenUserIds);
        return currentGenerationUserIds;
    }
}
