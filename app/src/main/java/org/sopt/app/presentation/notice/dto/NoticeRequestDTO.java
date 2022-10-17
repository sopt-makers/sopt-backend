package org.sopt.app.presentation.notice.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoticeRequestDTO {
    private String title; // 공지제목
    private String contents; // 공지 내용
    private String part; // 파트
    private String scope; // 범위 (전체 공개 , 회원공개)
}
