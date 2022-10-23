package org.sopt.app.application.alert;

import lombok.RequiredArgsConstructor;
import org.sopt.app.application.alert.command.SaveAlertCommand;
import org.sopt.app.common.exception.UserNotFoundException;
import org.sopt.app.domain.entity.Alert;
import org.sopt.app.domain.entity.User;
import org.sopt.app.domain.enums.Parts;
import org.sopt.app.interfaces.postgres.UserJpaRepository;
import org.sopt.app.interfaces.postgres.alert.AlertRepository;
import org.sopt.app.presentation.alert.dto.PartResponseDTO;
import org.sopt.app.presentation.alert.dto.FindUserPartResponseDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AlertService implements AlertUseCase {

    private final UserJpaRepository userJpaRepository;
    private final AlertRepository alertRepository;

    public PartResponseDTO findPart() {
        return PartResponseDTO.builder()
                .parts(Parts.getParts())
                .build();
    }

    @Override
    public FindUserPartResponseDTO findPartByUserId(Long userId) {
        Alert alert = alertRepository.findAlertByTmpUserId(userId);
        List<String> topics = alert.getTopic();

        List<FindUserPartResponseDTO.PartsDTO> userParts = Parts.getParts().stream().map(
                part -> {
                    if (topics.contains(part)) {
                        return FindUserPartResponseDTO.PartsDTO.builder()
                                .part(part)
                                .isActive(true)
                                .build();
                    } else {
                        return FindUserPartResponseDTO.PartsDTO.builder()
                                .part(part)
                                .isActive(false)
                                .build();
                    }
                }
        ).toList();

        return FindUserPartResponseDTO.builder()
                .parts(userParts)
                .build();
    }

    @Override
    public void saveParts(SaveAlertCommand command) {
        User user = userJpaRepository.findById(command.getUserId())
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
