package org.sopt.app.application.meeting;

import java.util.List;
import lombok.Builder;
import org.sopt.app.application.meeting.CrewMeetingResponse.CrewMeeting;
import org.sopt.app.domain.enums.Part;

@Builder
public record MeetingResponse(
    Long id,
    String title,
    String category,
    Boolean canJoinOnlyActiveGeneration,
    List<String> joinableParts,
    Boolean canJoinAllParts,
    MeetingStatus status,
    String imageUrl
) {
    public static MeetingResponse of(final CrewMeeting crewMeeting) {
        return MeetingResponse.builder()
                .id(crewMeeting.id())
                .title(crewMeeting.title())
                .category(crewMeeting.category())
                .canJoinOnlyActiveGeneration(crewMeeting.canJoinOnlyActiveGeneration())
                .joinableParts(crewMeeting.joinableParts())
                .canJoinAllParts(crewMeeting.joinableParts().size() == Part.values().length)
                .status(crewMeeting.status())
                .imageUrl(crewMeeting.imageUrl())
                .build();
    }
}
