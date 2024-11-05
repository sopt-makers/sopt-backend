package org.sopt.app.facade;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.home.ActivityDurationCalculator;
import org.sopt.app.application.playground.PlaygroundAuthService;
import org.sopt.app.application.description.DescriptionInfo.MainDescription;
import org.sopt.app.application.description.DescriptionService;
import org.sopt.app.domain.entity.User;
import org.sopt.app.presentation.home.HomeDescriptionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HomeFacade {

    private final DescriptionService descriptionService;
    private final PlaygroundAuthService playgroundAuthService;

    @Transactional(readOnly = true)
    @Deprecated
    public MainDescription getMainDescriptionForUser(User user) {
        val userActiveInfo = playgroundAuthService.getPlaygroundUserActiveInfo(user.getPlaygroundToken(), user.getPlaygroundId());
        return descriptionService.getMainDescription(userActiveInfo.status());
    }

    @Transactional(readOnly = true)
    public HomeDescriptionResponse getHomeMainDescription(User user) {
        List<Long> ownGenerations = playgroundAuthService.getOwnPlaygroundProfile(user.getPlaygroundToken())
                .getAllGenerations();
        ActivityDurationCalculator calculator = ActivityDurationCalculator.of(ownGenerations);
        return HomeDescriptionResponse.of(
                user.getUsername(),
                calculator.getActivityDuration()
        );
    }
}
