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
import org.sopt.app.presentation.poke.PokeResponse.AllRelationFriendList;
import org.sopt.app.presentation.poke.PokeResponse.Friend;
import org.sopt.app.presentation.poke.PokeResponse.FriendList;
import org.sopt.app.presentation.poke.PokeResponse.PokeMessageList;
import org.sopt.app.presentation.poke.PokeResponse.PokeToMeHistoryList;
import org.sopt.app.presentation.poke.PokeResponse.RecommendedFriendsRequest;
import org.sopt.app.presentation.poke.PokeResponse.SimplePokeProfile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/poke")
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
public class PokeController {

    private final PokeFacade pokeFacade;

    @GetMapping("/random-user")
    public ResponseEntity<List<PokeResponse.SimplePokeProfile>> getRandomUserForNew(
            @AuthenticationPrincipal User user
    ) {
        val result = pokeFacade.getRecommendUserForNew(
                user.getPlaygroundToken(),
                user.getPlaygroundId(),
                user.getId()
        );
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "신규 유저인지 조회")
    @GetMapping("/new")
    public ResponseEntity<PokeResponse.IsNew> getPokeList(
            @AuthenticationPrincipal User user
    ) {
        val result = pokeFacade.getIsNewUser(user.getId());
        val response = PokeResponse.IsNew.of(result);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/friend/random-user")
    public ResponseEntity<List<Friend>> getRandomFriendsOfUsersFriend(
            @AuthenticationPrincipal User user
    ) {
        val result = pokeFacade.getRecommendFriendsOfUsersFriend(user);
        return ResponseEntity.ok(result);
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
        val response = PokeMessageList.of(pokingMessageHeader, messages);
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
        val pokeHistoryId = pokeFacade.pokeFriend(
                user.getId(), pokedUserId, messageRequest.getMessage(), messageRequest.getIsAnonymous()
        );
        val response = pokeFacade.getPokeHistoryProfile(user, pokedUserId, pokeHistoryId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "친구를 찔러보세요 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("/friend")
    public ResponseEntity<List<PokeResponse.SimplePokeProfile>> getFriendList(
            @AuthenticationPrincipal User user
    ) {
        val result = pokeFacade.getFriend(user);
        return ResponseEntity.ok(result);
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
        val response = pokeFacade.getMostRecentPokeMeHistory(user);
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
            @PageableDefault(size = 25, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        val response = pokeFacade.getAllPokeMeHistory(user, pageable);
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
            val newFriends = pokeFacade.getTwoFriendByFriendship(user, Friendship.NEW_FRIEND);
            val newFriendsSize = pokeFacade.getFriendSizeByFriendship(user.getId(), Friendship.NEW_FRIEND);
            val bestFriends = pokeFacade.getTwoFriendByFriendship(user, Friendship.BEST_FRIEND);
            val bestFriendsSize = pokeFacade.getFriendSizeByFriendship(user.getId(), Friendship.BEST_FRIEND);
            val soulMates = pokeFacade.getTwoFriendByFriendship(user, Friendship.SOULMATE);
            val soulMatesSize = pokeFacade.getFriendSizeByFriendship(user.getId(), Friendship.SOULMATE);

            val response = AllRelationFriendList.of(
                    newFriends, newFriendsSize,
                    bestFriends, bestFriendsSize,
                    soulMates, soulMatesSize,
                    newFriendsSize + bestFriendsSize + soulMatesSize
            );
            return ResponseEntity.ok(response);
        }
        Friendship targetFriendship = Friendship.getFriendshipByValue(type);
        val friends = pokeFacade.getAllFriendByFriendship(user, targetFriendship, pageable);
        return ResponseEntity.ok(friends);
    }

    @Operation(summary = "친구 추천 통합 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("/random")
    public ResponseEntity<RecommendedFriendsRequest> getRandomFriendsByFriendRecommendType(
            @AuthenticationPrincipal User user,
            @RequestParam(value = "randomType") List<FriendRecommendType> typeList,
            @RequestParam(value = "size") int size
    ) {
        return ResponseEntity.ok(
                pokeFacade.getRecommendedFriendsByTypeList(typeList, size, user)
        );
    }

}
