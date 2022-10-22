package org.sopt.app.application.alert;

import org.sopt.app.application.alert.command.SaveAlertCommand;

public interface AlertUseCase {

    void saveParts(SaveAlertCommand command);
}
