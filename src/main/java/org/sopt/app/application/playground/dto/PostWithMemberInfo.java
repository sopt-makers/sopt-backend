package org.sopt.app.application.playground.dto;

public interface PostWithMemberInfo {
    Object withMemberDetail(String name, String profileImage);
    Long getId();
}