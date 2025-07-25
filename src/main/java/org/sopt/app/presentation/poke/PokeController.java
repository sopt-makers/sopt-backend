package org.sopt.app.presentation.poke;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.domain.entity.User;
import org.sopt.app.domain.enums.FriendRecommendType;
import org.sopt.app.domain.enums.Friendship;
import org.sopt.app.facade.PokeFacade;
import org.sopt.app.presentation.poke.PokeResponse.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/poke")
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
public class PokeController {

    private final PokeFacade pokeFacade;

    @Operation(summary = "신규 유저인지 조회")
    @GetMapping("/new")
    public ResponseEntity<PokeResponse.IsNew> getPokeList(
            @AuthenticationPrincipal Long userId
    ) {
        val result = pokeFacade.getIsNewUser(userId);
        return ResponseEntity.ok(new IsNew(result));
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
        val pokingMessageHeader = pokeFacade.getPokingMessageHeader(messageType);
        val response = new PokeMessageList(pokingMessageHeader, messages);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "찌르기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @PutMapping("/{userId}")
    public ResponseEntity<SimplePokeProfile> orderPoke(
            @AuthenticationPrincipal Long userId,
            @PathVariable("userId") Long pokedUserId,
            @RequestBody PokeRequest.PokeMessageRequest messageRequest
    ) {
        val pokeHistoryId = pokeFacade.pokeFriend(
                userId, pokedUserId, messageRequest.getMessage(), messageRequest.getIsAnonymous()
        );
        val response = pokeFacade.getPokeHistoryProfile(userId, pokedUserId, pokeHistoryId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "친구를 찔러보세요 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("/friend")
    public ResponseEntity<List<PokeResponse.SimplePokeProfile>> getFriendList(
            @AuthenticationPrincipal Long userId
    ) {
        val result = pokeFacade.getFriend(userId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "누가 나를 찔렀어요 조회 - 단일")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("/to/me")
    public ResponseEntity<SimplePokeProfile> getPokeMeMostRecent(
            @AuthenticationPrincipal Long userId
    ) {
        val response = pokeFacade.getMostRecentPokeMeHistory(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "누가 나를 찔렀어요 조회 - 리스트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("/to/me/list")
    public ResponseEntity<PokeToMeHistoryList> getAllOfPokeMe(
            @AuthenticationPrincipal Long userId,
            @PageableDefault(size = 25, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        val response = pokeFacade.getAllPokeMeHistory(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "친구 조회 - 리스트 (전체 카테고리)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("/friend/list")
    public ResponseEntity<FriendList> getFriendsForEachRelation(
            @AuthenticationPrincipal Long userId,
            @RequestParam(value = "type", required = false) String type,
            @PageableDefault(size = 25) Pageable pageable
    ) {
        if (Objects.isNull(type)) {
            val newFriends = pokeFacade.getTwoFriendByFriendship(userId, Friendship.NEW_FRIEND);
            val newFriendsSize = pokeFacade.getFriendSizeByFriendship(userId, Friendship.NEW_FRIEND);
            val bestFriends = pokeFacade.getTwoFriendByFriendship(userId, Friendship.BEST_FRIEND);
            val bestFriendsSize = pokeFacade.getFriendSizeByFriendship(userId, Friendship.BEST_FRIEND);
            val soulMates = pokeFacade.getTwoFriendByFriendship(userId, Friendship.SOULMATE);
            val soulMatesSize = pokeFacade.getFriendSizeByFriendship(userId, Friendship.SOULMATE);

            val response = AllRelationFriendList.builder()
                    .newFriend(newFriends).newFriendSize(newFriendsSize)
                    .bestFriend(bestFriends).bestFriendSize(bestFriendsSize)
                    .soulmate(soulMates).soulmateSize(soulMatesSize)
                    .totalSize(newFriendsSize + bestFriendsSize + soulMatesSize)
                    .build();
            return ResponseEntity.ok(response);
        }
        Friendship targetFriendship = Friendship.getFriendshipByValue(type);
        val friends = pokeFacade.getAllFriendByFriendship(userId, targetFriendship, pageable);
        return ResponseEntity.ok(friends);
    }

    @Operation(summary = "친구 추천 통합 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("/random")
    public ResponseEntity<RecommendedFriendsRequest> getRandomFriendsByFriendRecommendType(
            @AuthenticationPrincipal Long userId,
            @RequestParam(value = "randomType", required = false) List<FriendRecommendType> typeList,
            @RequestParam(value = "size") int size
    ) {
        return ResponseEntity.ok(
                pokeFacade.getRecommendedFriendsByTypeList(typeList, size, userId)
        );
    }

}
