package org.sopt.app.presentation.home;

public record MeetingParamRequest(
        Long playgroundId,
        int page,
        int take,
        String category
) {

}
