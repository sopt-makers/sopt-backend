package org.sopt.app.application.alert;

import org.sopt.app.application.alert.command.SaveAlertCommand;
import org.sopt.app.presentation.alert.dto.FindUserPartResponseDTO;

public interface AlertUseCase {

    FindUserPartResponseDTO findPartByUserId(Long userId);

    void saveParts(SaveAlertCommand command);
}
