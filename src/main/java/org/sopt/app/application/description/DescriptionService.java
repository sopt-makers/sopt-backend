package org.sopt.app.application.description;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.domain.enums.UserStatus;
import org.sopt.app.interfaces.postgres.MainDescriptionRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DescriptionService {

    private final MainDescriptionRepository mainDescriptionRepository;

    public DescriptionInfo.MainDescription getMainDescription(UserStatus userStatus) {
        val mainDescription = mainDescriptionRepository.findAll().get(0);

        return userStatus == UserStatus.ACTIVE ?
                DescriptionInfo.ActiveMainDescription.builder()
                        .activeTopDescription(mainDescription.getActiveTopDescription())
                        .activeBottomDescription(mainDescription.getActiveBottomDescription())
                        .build()
                :
                DescriptionInfo.InactiveMainDescription.builder()
                        .inactiveTopDescription(mainDescription.getInactiveTopDescription())
                        .inactiveBottomDescription(mainDescription.getInactiveBottomDescription())
                        .build();
    }
}
