package org.sopt.app.application.meeting;

import java.util.List;

public record CrewMeetingResponse(
        List<Meeting> meetings
) {
    public record Meeting(
        Long id,
        String title,
        Boolean canJoinOnlyActiveGeneration,
        MeetingStatus status,
        String imageUrl,
        String category,
        List<String> joinableParts,
        Boolean isBlockedMeeting
    ){

    }
}
