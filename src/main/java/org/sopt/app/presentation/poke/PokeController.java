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
import org.sopt.app.domain.enums.Friendship;
import org.sopt.app.facade.PokeFacade;
import org.sopt.app.presentation.poke.PokeResponse.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Objects;

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
    public ResponseEntity<List<PokeProfile>> getRandomUserForNew(
        @AuthenticationPrincipal User user
    ) {
        val result = pokeFacade.getRecommendUserForNew(
            user.getPlaygroundToken(),
            user.getPlaygroundId()
        );
        val response = result.stream().map(
            profile -> PokeProfile.of(
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
                    profile -> PokeProfile.of(
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

    @Operation(summary = "찌르기 메시지 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("/message")
    public ResponseEntity<PokeMessageList> getPokeMessages(
            @RequestParam("messageType") String messageType
    ) {
        val messages = pokeFacade.getPokingMessages(messageType);
        val response = PokeMessageList.of(messages);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "찌르기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @PutMapping("/{userId}")
    public ResponseEntity<SimplePokeProfile> orderPoke(
            @AuthenticationPrincipal User user,
            @PathVariable("userId") Long pokedUserId,
            @RequestBody PokeRequest.PokeMessageRequest messageRequest
    ) {
        pokeFacade.pokeFriend(user.getId(), pokedUserId, messageRequest.getMessage());
        pokeFacade.applyFriendship(user.getId(), pokedUserId);
        val response = pokeFacade.getPokeHistoryProfileWith(user, pokedUserId);
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
    public ResponseEntity<List<PokeProfile>> getFriendList(
            @AuthenticationPrincipal User user
    ) {
        val result = pokeFacade.getFriend(user);
        val response = result.stream().map(
                profile -> PokeProfile.of(
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

    @Operation(summary = "누가 나를 찔렀어요 조회 - 단일")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("/to/me")
    public ResponseEntity<SimplePokeProfile> getPokeMeMostRecent(
            @AuthenticationPrincipal User user
    ) {
        val mostRecentPokeUserId = pokeFacade.getFirstUserIdOfPokeMeReplyYet(user.getId());
        if (Objects.isNull(mostRecentPokeUserId)) {
            return ResponseEntity.ok(null);
        }
        val response = pokeFacade.getPokeHistoryProfileWith(user, mostRecentPokeUserId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "누가 나를 찔렀어요 조회 - 리스트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("/to/me/list")
    public ResponseEntity<PokeToMeHistoryList> getAllOfPokeMe(
            @AuthenticationPrincipal User user,
            // TODO : Notification List 에서도 기본 Size 요구사항이 25 개면 yaml 에서 속성 관리하기
            @PageableDefault(size = 25, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
            ) {
        val response = pokeFacade.getAllUserIdsOfPokeMe(user, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "친구 조회 - 리스트 (전체 카테고리)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("/friend/list")
    public ResponseEntity<FriendList> getFriendsForEachRelation(
            @AuthenticationPrincipal User user,
            @RequestParam(value = "type", required = false) String type,
            @PageableDefault(size = 25) Pageable pageable
    ) {
        if (Objects.isNull(type)) {
            val newFriends = pokeFacade.getFriendByFriendship(user, Friendship.NEW_FRIEND);
            val bestFriends = pokeFacade.getFriendByFriendship(user, Friendship.BEST_FRIEND);
            val soulMates = pokeFacade.getFriendByFriendship(user, Friendship.SOULMATE);
            val response = AllRelationFriendList.of(
                    newFriends, newFriends.size(),
                    bestFriends, bestFriends.size(),
                    soulMates, soulMates.size()
            );
            return ResponseEntity.ok(response);
        }
        Friendship targetFriendship = Friendship.getFriendshipByValue(type);
        val friends = pokeFacade.getFriendByFriendship(user, targetFriendship, pageable);
        return ResponseEntity.ok(friends);
    }

}
