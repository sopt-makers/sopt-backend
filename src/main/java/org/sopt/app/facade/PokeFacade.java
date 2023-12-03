package org.sopt.app.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.auth.PlaygroundAuthService;
import org.sopt.app.application.user.UserInfo.PorkProfile;
import org.sopt.app.application.user.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PokeFacade {
    private final PlaygroundAuthService playgroundAuthService;
    private final UserService userService;

    private final int RECOMMEND_USER_NUM_FOR_NEW = 6;

    public List<PorkProfile> getRecommendUserForNew(String playgroundToken, Long userPlaygroundId) {
        val playgroundUserIds = playgroundAuthService.getPlayGroundUserIds(playgroundToken);
        val recommendUserIds = pickRandomUserIds(playgroundUserIds.getUserIds(), userPlaygroundId, RECOMMEND_USER_NUM_FOR_NEW);
        val playgroundProfiles = playgroundAuthService.getPlaygroundProfiles(recommendUserIds);
        val userProfiles = userService.getUserProfiles(recommendUserIds);
        return userService.combinePokeProfileList(userProfiles, playgroundProfiles);
    }

    private List<Long> pickRandomUserIds(List<Long> playgroundUserIds, Long userPlaygroundId, int limitNum) {
        List<Long> copiedList = new ArrayList<>(playgroundUserIds);
        copiedList.remove(userPlaygroundId);
        Collections.shuffle(copiedList, new Random());
        return copiedList.stream().limit(limitNum).collect(Collectors.toList());
    }
}
