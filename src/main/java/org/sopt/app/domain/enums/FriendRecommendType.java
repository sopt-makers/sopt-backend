package org.sopt.app.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FriendRecommendType {
    GENERATION("나와 같은 기수예요"),
    MBTI("나와 MBTI가 같은 사람이에요"),
    UNIVERSITY("나와 같은 학교예요"),
    ALL(""), // 기수, MBTI, 학교를 모두 추천
    ALL_USER(""); // 모든 앱 유저를 추천

    private final String recommendTitle;
}
