package org.sopt.app.presentation.rank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.app.domain.entity.Mission;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FindRankResponseDto {
    private Long userId;
    private String nickname;
    private String profileMessage;

    private List<Mission> userMissions;

}
