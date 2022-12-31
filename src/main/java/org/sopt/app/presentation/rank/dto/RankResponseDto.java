package org.sopt.app.presentation.rank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RankResponseDto {
    private Integer rank;
    private String nickname;
    private Long point;

    private String profileMessage;
}
