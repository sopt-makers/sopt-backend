package org.sopt.app.v1.presentation.rank.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.app.domain.entity.Mission;

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
