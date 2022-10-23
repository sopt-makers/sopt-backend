package org.sopt.app.presentation.alert.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SavePartsRequestDTO {
    private List<String> parts;
    private Boolean active;
}
