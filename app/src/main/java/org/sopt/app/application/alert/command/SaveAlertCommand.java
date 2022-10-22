package org.sopt.app.application.alert.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.app.presentation.alert.dto.SavePartsDTO;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveAlertCommand {
    private Long userId;
    private SavePartsDTO partsDto;
}

