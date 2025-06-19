package org.sopt.app.application.platform.dto;


import java.util.List;

public record PlatformUserInfoResponse(
        int userId,
        String name,
        String profileImage,
        String birthday,
        String phone,
        String email,
        int lastGeneration,
        List<SoptActivities> soptActivities
) {
    public record SoptActivities(
            int activityId,
            int generation,
            String part,
            String team
    ){
    }
}
