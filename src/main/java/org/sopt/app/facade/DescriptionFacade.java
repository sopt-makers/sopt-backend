package org.sopt.app.facade;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.playground.PlaygroundAuthService;
import org.sopt.app.application.description.DescriptionInfo.MainDescription;
import org.sopt.app.application.description.DescriptionService;
import org.sopt.app.domain.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DescriptionFacade {

    private final DescriptionService descriptionService;
    private final PlaygroundAuthService playgroundAuthService;

    @Transactional(readOnly = true)
    public MainDescription getMainDescriptionForUser(User user) {
        val userActiveInfo = playgroundAuthService.getPlaygroundUserActiveInfo(user.getPlaygroundToken(), user.getPlaygroundId());
        return descriptionService.getMainDescription(userActiveInfo.status());
    }
}
