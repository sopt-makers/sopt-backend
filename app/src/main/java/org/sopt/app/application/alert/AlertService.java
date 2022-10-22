package org.sopt.app.application.alert;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.alert.command.SaveAlertCommand;
import org.sopt.app.common.exception.UserNotFoundException;
import org.sopt.app.domain.entity.Alert;
import org.sopt.app.domain.enums.Parts;
import org.sopt.app.interfaces.postgres.UserJpaRepository;
import org.sopt.app.interfaces.postgres.alert.AlertRepository;
import org.sopt.app.presentation.alert.dto.PartsDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertService implements AlertUseCase {

    private final UserJpaRepository userJpaRepository;
    private final AlertRepository alertRepository;

    public PartsDTO findPart() {

        //ENUM 값을 통해 part 들을 받아온다.
        List<String> partString = new ArrayList<>();
        for (Parts part : Parts.values()) {
            partString.add(part.toString());
        }

        return PartsDTO.builder()
                .parts(partString)
                .build();
    }

    public void saveParts(SaveAlertCommand command) {
        val user = userJpaRepository.findById(command.getUserId())
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 유저 아이디입니다."));
        if (command.getPartsDto().getActive()) {
            alertRepository.save(Alert.builder()
                    .tmpUserId(user.getId())
                    .topic(command.getPartsDto().getParts())
                    .active(true)
                    .build());
        } else {
            alertRepository.save(Alert.builder()
                    .tmpUserId(user.getId())
                    .build());
        }
    }
}
