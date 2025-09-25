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

    /**
     * generation 최댓값 기준으로 최신 활동을 찾아 반환.
     * lastGeneration 필드와 관계없이 계산.
     */
    public SoptActivities getLatestActivity() {
        if (soptActivities == null) return null;
        return soptActivities.stream()
            .max(java.util.Comparator.comparingInt(SoptActivities::generation))
            .orElse(null);
    }
}
