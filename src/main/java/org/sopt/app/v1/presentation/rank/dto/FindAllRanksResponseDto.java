package org.sopt.app.v1.presentation.rank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindAllRanksResponseDto {
    private Integer rank;
    private Long userId;
    private String nickname;
    private Long point;

    private String profileMessage;
}
