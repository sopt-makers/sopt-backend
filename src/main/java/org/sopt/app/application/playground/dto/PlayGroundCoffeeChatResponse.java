package org.sopt.app.application.playground.dto;

import java.util.List;

public record PlayGroundCoffeeChatResponse(
        Long memberId,
        String bio,
        List<String> topicTypeList,
        String profileImage,
        String name,
        String career,
        String organization,
        String companyJob,
        List<String> soptActivities,
        boolean isMine,
        boolean isBlind
) {
}