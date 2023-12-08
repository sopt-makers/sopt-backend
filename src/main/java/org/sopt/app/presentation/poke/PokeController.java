package org.sopt.app.presentation.poke;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.poke.PokeHistoryService;
import org.sopt.app.domain.entity.User;
import org.sopt.app.facade.PokeFacade;
import org.springframework.http.HttpStatus;
import org.sopt.app.presentation.poke.PokeResponse.Friend;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/poke")
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
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
                profile.getPart(),
                profile.getIsAlreadyPoked()
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
    public ResponseEntity<List<Friend>> getRandomFriendsOfUsersFriend(
        @AuthenticationPrincipal User user
    ) {
        val result = pokeFacade.getRecommendFriendsOfUsersFriend(user);
        val response = result.stream().map(
            friend -> PokeResponse.Friend.of(
                friend.getId(),
                friend.getName(),
                friend.getProfileImage(),
                friend.getFriendList().stream().map(
                    profile -> PokeResponse.PokeProfile.of(
                        profile.getUserId(),
                        profile.getProfileImage(),
                        profile.getName(),
                        profile.getGeneration(),
                        profile.getPart(),
                        profile.getIsAlreadyPoked()
                    )
                ).toList()
            )
        ).toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "찌르기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @PutMapping("/{userId}")
    public ResponseEntity<PokeResponse.SimplePokeProfile> orderPoke(
            @AuthenticationPrincipal User user,
            @PathVariable("userId") Long pokedUserId,
            @RequestBody PokeRequest.PokeMessageRequest messageRequest
    ) {
        pokeHistoryService.checkUserOverDailyPokeLimit(user.getId());
        pokeFacade.applyFriendship(user.getId(), pokedUserId);
        pokeFacade.pokeFriend(user.getId(), pokedUserId, messageRequest.getMessage());

        val pokedUserInfo = pokeFacade.getPokedUserInfo(user, pokedUserId);

        val pokeInfo = pokeFacade.getPokeInfo(user, pokedUserId);
        val response = PokeResponse.SimplePokeProfile.of(
                pokedUserInfo.getUserId(),
                pokedUserInfo.getProfileImage(),
                pokedUserInfo.getName(),
                pokeInfo.getMessage(),
                pokedUserInfo.getActivity(),
                pokedUserInfo.getRelation().getPokeCount(),
                pokedUserInfo.getRelation().getRelationName(),
                pokedUserInfo.getMutualFriendNames(),
      pokedUserInfo.getRelation().getPokeCount() == 0,
                pokeInfo.getIsReply()
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @Operation(summary = "친구를 찔러보세요 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("/friend")
    public ResponseEntity<List<PokeResponse.PokeProfile>> getFriendList(
            @AuthenticationPrincipal User user
    ) {
        val result = pokeFacade.getFriend(user.getId());
        val response = result.stream().map(
                profile -> PokeResponse.PokeProfile.of(
                        profile.getUserId(),
                        profile.getProfileImage(),
                        profile.getName(),
                        profile.getGeneration(),
                        profile.getPart(),
                        profile.getIsAlreadyPoked()
                )
        ).toList();
        return ResponseEntity.ok(response);
    }
}
