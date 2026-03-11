package org.sopt.app.application.platform.dto;


import java.util.List;

public record PlatformUserInfoResponse(
        int userId,
        String name,
        String profileImage,
        String birthday,
        String phone,
        String email,
        int lastGeneration, // 솝트 기수 기준으로 내려주긴 함.
        List<SoptActivities> soptActivities
) {
    public record SoptActivities(
            int activityId,
            int generation,
            String part,
            String team,
            Boolean isSopt
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

    /**
     * isSopt=true인 활동 중 가장 최신 기수의 SOPT 정규 활동을 반환.
     * 반환값이 null이면 현재 기수에 솝트 활동이 없음.
     */
    public SoptActivities getLatestSoptActivity() {
        if (soptActivities == null) return null;
        return soptActivities.stream()
            .filter(a -> Boolean.TRUE.equals(a.isSopt()))
            .max(java.util.Comparator.comparingInt(SoptActivities::generation))
            .orElse(null);
    }

    public int getLastSoptGeneration(){
        if (soptActivities == null || soptActivities.isEmpty()) return 0;

        return soptActivities.stream()
            .filter(SoptActivities::isSopt)
            .map(SoptActivities::generation)
            .max(Integer::compareTo)
            .orElse(0);
    }
}
