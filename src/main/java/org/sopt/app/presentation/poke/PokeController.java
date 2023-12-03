package org.sopt.app.presentation.poke;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.poke.PokeHistoryService;
import org.sopt.app.domain.entity.User;
import org.sopt.app.facade.PokeFacade;
import org.sopt.app.presentation.poke.PokeResponse.Friend;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/poke")
@RequiredArgsConstructor
public class PokeController {

    private final PokeFacade pokeFacade;
    private final PokeHistoryService pokeHistoryService;

    @GetMapping("/random-user")
    public ResponseEntity<List<PokeResponse.PokeProfile>> getRandomUserForNew(
        @AuthenticationPrincipal User user
    ) {
        val result = pokeFacade.getRecommendUserForNew(
            user.getPlaygroundToken(),
            user.getPlaygroundId()
        );
        val response = result.stream().map(
            profile -> PokeResponse.PokeProfile.of(
                profile.getUserId(),
                profile.getProfileImage(),
                profile.getName(),
                profile.getGeneration(),
                profile.getPart()
            )
        ).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/new")
    public ResponseEntity<PokeResponse.IsNew> getPokeList(
        @AuthenticationPrincipal User user
    ) {
        val result = pokeHistoryService.isNewPoker(user.getId());
        val response = PokeResponse.IsNew.of(result);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/friend/random-user")
    public ResponseEntity<List<Friend>> test(
        @AuthenticationPrincipal User user
    ) {
        val result = pokeFacade.getRecommendFriendsOfUsersFriend(user);
        val response = result.stream().map(
            friend -> PokeResponse.Friend.of(
                friend.getFriendId(),
                friend.getFriendName(),
                friend.getFriendProfileImage(),
                friend.getFriendList().stream().map(
                    profile -> PokeResponse.PokeProfile.of(
                        profile.getUserId(),
                        profile.getProfileImage(),
                        profile.getName(),
                        profile.getGeneration(),
                        profile.getPart()
                    )
                ).toList()
            )
        ).toList();
        return ResponseEntity.ok(response);
    }
}
