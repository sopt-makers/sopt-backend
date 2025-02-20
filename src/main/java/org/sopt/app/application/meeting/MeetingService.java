package org.sopt.app.application.meeting;

import static org.sopt.app.application.playground.PlaygroundHeaderCreator.createAuthorizationHeaderByInternalPlaygroundToken;

import lombok.RequiredArgsConstructor;
import org.sopt.app.presentation.home.MeetingParamRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeetingService {
    private final CrewClient crewClient;

    public CrewMeetingResponse getAllMeetings(MeetingParamRequest request) {
        return crewClient.getAllMeetings(
                createAuthorizationHeaderByInternalPlaygroundToken(),
                request.playgroundId(),
                request.page(),
                request.take(),
                request.category(),
                false
        );
    }
}
