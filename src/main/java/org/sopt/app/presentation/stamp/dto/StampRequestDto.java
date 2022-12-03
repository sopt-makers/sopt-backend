package org.sopt.app.presentation.stamp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StampRequestDto {

  private String contents; // 스탬프 내용
}
