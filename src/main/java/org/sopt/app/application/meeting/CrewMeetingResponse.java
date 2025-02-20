package org.sopt.app.application.meeting;

import java.util.List;

public record CrewMeetingResponse(
        List<CrewMeeting> meetings
) {
    public record CrewMeeting(
        Long id,
        String title,
        Boolean canJoinOnlyActiveGeneration,
        MeetingStatus status,
        String imageUrl,
        String category,
        List<CrewPart> joinableParts,
        Boolean isBlockedMeeting
    ){

    }
}
