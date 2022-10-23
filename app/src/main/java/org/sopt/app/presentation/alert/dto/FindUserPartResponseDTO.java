package org.sopt.app.presentation.alert.dto;

import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
public class FindUserPartResponseDTO {
    public List<PartsDTO> parts;


    @Builder
    @Getter
    public static class PartsDTO {
        public String part;
        public Boolean isActive;
    }
}
