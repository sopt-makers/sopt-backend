package org.sopt.app.presentation.mission.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GetAllMissionResponseDto {
    private Long id;
    private String title;
    private Integer level;
    private List<String> profileImage;
    private Boolean isCompleted;


    @Builder
    public GetAllMissionResponseDto(
            Long id, String title, Integer level, List<String> profileImage, Boolean isCompleted) {
        this.id = id;
        this.title = title;
        this.level = level;
        this.profileImage = profileImage;
        this.isCompleted = isCompleted;
    }
}
