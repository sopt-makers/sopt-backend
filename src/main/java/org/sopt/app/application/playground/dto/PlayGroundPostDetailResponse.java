package org.sopt.app.application.playground.dto;

public record PlayGroundPostDetailResponse(
        Member member, AnonymousProfile anonymousProfile
) {
    public record Member(
            String name,
            String profileImage
    ) {
    }

    public record AnonymousProfile(
            String nickname,
            String profileImgUrl
    ) {
    }

}