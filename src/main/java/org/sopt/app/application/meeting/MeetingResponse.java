package org.sopt.app.application.meeting;

import lombok.Builder;

@Builder
public record MeetingResponse(
    Long meetingId,
    String title,
    MeetingCategory category,
    MeetingStatus status,
    String imageUrl,
    Boolean canJoinOnlyActiveGeneration
) {
    public static MeetingResponse eventActiveDummy() {
        return MeetingResponse.builder()
                .meetingId(1L)
                .title("[35기 솝커톤] 서버 파트 신청")
                .category(MeetingCategory.EVENT)
                .status(MeetingStatus.ACTIVE)
                .imageUrl("https://makers-web-img.s3.ap-northeast-2.amazonaws.com/meeting/2024/11/14/78d48e33-f1d7-474f-a357-117b75a8cb90.png")
                .canJoinOnlyActiveGeneration(false)
                .build();
    }

    public static MeetingResponse studyRecruitingDummy() {
        return MeetingResponse.builder()
                .meetingId(2L)
                .title("모집중이고 활동 기수만 참여하는 스터디")
                .category(MeetingCategory.STUDY)
                .status(MeetingStatus.RECRUITING)
                .imageUrl("https://makers-web-img.s3.ap-northeast-2.amazonaws.com/meeting/2024/11/14/78d48e33-f1d7-474f-a357-117b75a8cb90.png")
                .canJoinOnlyActiveGeneration(true)
                .build();
    }

    public static MeetingResponse studyPreRecruitingDummy() {
        return MeetingResponse.builder()
                .meetingId(3L)
                .title("모집 이전이고 모든 기수가 참여하는 스터디")
                .category(MeetingCategory.STUDY)
                .status(MeetingStatus.PRE_RECRUITING)
                .imageUrl("https://makers-web-img.s3.ap-northeast-2.amazonaws.com/meeting/2024/11/14/78d48e33-f1d7-474f-a357-117b75a8cb90.png")
                .canJoinOnlyActiveGeneration(false)
                .build();
    }

    public static MeetingResponse studyClosedDummy() {
        return MeetingResponse.builder()
                .meetingId(4L)
                .title("모집이 끝나고 모든 기수가 참여하는 스터디")
                .category(MeetingCategory.STUDY)
                .status(MeetingStatus.CLOSED)
                .imageUrl("https://makers-web-img.s3.ap-northeast-2.amazonaws.com/meeting/2024/11/14/78d48e33-f1d7-474f-a357-117b75a8cb90.png")
                .canJoinOnlyActiveGeneration(false)
                .build();
    }

    public static MeetingResponse studyActiveDummy() {
        return MeetingResponse.builder()
                .meetingId(5L)
                .title("활동중이고 활동 기수만 참여하는 스터디")
                .category(MeetingCategory.STUDY)
                .status(MeetingStatus.ACTIVE)
                .imageUrl("https://makers-web-img.s3.ap-northeast-2.amazonaws.com/meeting/2024/11/14/78d48e33-f1d7-474f-a357-117b75a8cb90.png")
                .canJoinOnlyActiveGeneration(true)
                .build();
    }
}
