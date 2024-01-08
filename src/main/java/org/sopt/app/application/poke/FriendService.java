package org.sopt.app.application.poke;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.sopt.app.common.exception.NotFoundException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.Friend;
import org.sopt.app.domain.enums.Friendship;
import org.sopt.app.interfaces.postgres.FriendRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;

    public List<Long> findAllFriendIdsByUserIdRandomly(Long userId, int limitNum) {
        return friendRepository.getFriendRandom(userId, limitNum);
    }

    public List<Friend> findAllFriendsByFriendship(Long userId, Integer lowerLimit, Integer upperLimit) {
        HashMap<Long, Integer> map = getPokeCountMap(userId);

        return map.entrySet().stream()
                .filter(entry -> entry.getValue() >= lowerLimit && entry.getValue() < upperLimit)
                .sorted(Collections.reverseOrder(java.util.Map.Entry.comparingByValue()))
                .map(entry -> Friend.builder()
                        .userId(userId)
                        .friendUserId(entry.getKey())
                        .pokeCount(entry.getValue())
                        .build())
                .toList();
    }

    public int findAllFriendSizeByFriendship(Long userId, Integer lowerLimit, Integer upperLimit) {
        HashMap<Long, Integer> map = getPokeCountMap(userId);

        val count = map.entrySet().stream()
                .filter(entry -> entry.getValue() >= lowerLimit && entry.getValue() < upperLimit)
                .count();

        return (int) count;
    }

    @NotNull
    private HashMap<Long, Integer> getPokeCountMap(Long userId) {
        HashMap<Long, Integer> map = new HashMap<>();

        val friendsPokeMe = friendRepository.findAllByFriendUserId(userId);

        for (Friend friend : friendsPokeMe) {
            map.put(friend.getUserId(), friend.getPokeCount());
        }

        val friendsPokeByMe = friendRepository.findAllByUserId(userId);

        for (Friend friend : friendsPokeByMe) {
            if (map.containsKey(friend.getFriendUserId())) {
                map.put(friend.getFriendUserId(), map.get(friend.getFriendUserId()) + friend.getPokeCount());
            } else {
                map.put(friend.getFriendUserId(), friend.getPokeCount());
            }
        }
        return map;
    }

    public Page<Friend> findAllFriendsByFriendship(Long userId, Integer lowerLimit, Integer upperLimit, Pageable pageable) {
        val map = getPokeCountMap(userId);
        val friendIds = map.entrySet().stream()
                .filter(entry -> entry.getValue() >= lowerLimit && entry.getValue() < upperLimit)
                .sorted(Collections.reverseOrder(java.util.Map.Entry.comparingByValue()))
                .map(Entry::getKey)
                .toList();

        return friendRepository.findAllByUserIdAndFriendUserIdInOrderByPokeCount(userId, friendIds, pageable);
    }




    public void registerFriendshipOf(Long userId, Long friendId) {
        Friend createdRelationUserToFriend = Friend.builder()
                .userId(userId)
                .friendUserId(friendId)
                .pokeCount(1)
                .build();
        friendRepository.save(createdRelationUserToFriend);
    }

    public void applyPokeCount(Long pokerId, Long pokedId) {
        Friend friendship = friendRepository.findByUserIdAndFriendUserId(pokerId, pokedId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.FRIENDSHIP_NOT_FOUND.getMessage()));
        friendship.addPokeCount();
    }

    public boolean isFriendEachOther(Long pokerId, Long pokedId) {
        Optional<Friend> pokerToPokedRelation = friendRepository.findByUserIdAndFriendUserId(pokerId, pokedId);
        Optional<Friend> pokedToPokerRelation = friendRepository.findByUserIdAndFriendUserId(pokedId, pokerId);
        return pokerToPokedRelation.isPresent() && pokedToPokerRelation.isPresent();
    }

    public PokeInfo.Relationship getRelationInfo(Long pokerId, Long pokedId) {
        Optional<Friend> friendshipFromPokerToPoked = friendRepository.findByUserIdAndFriendUserId(pokerId, pokedId);
        Optional<Friend> friendshipFromPokedToPoker = friendRepository.findByUserIdAndFriendUserId(pokedId, pokerId);
        int totalPokeNum = 0;
        if (friendshipFromPokerToPoked.isPresent()) {
            totalPokeNum += friendshipFromPokerToPoked.get().getPokeCount();
        }
        if (friendshipFromPokedToPoker.isPresent()) {
            totalPokeNum += friendshipFromPokedToPoker.get().getPokeCount();
        }
        return PokeInfo.Relationship.builder()
                .pokeNum(totalPokeNum)
                .relationName(decideRelationName(totalPokeNum))
                .build();
    }

    private String decideRelationName(Integer pokeCount) {
        if (pokeCount >= Friendship.SOULMATE.getLowerLimit()) {
            return Friendship.SOULMATE.getFriendshipName();
        }
        if (pokeCount >= Friendship.BEST_FRIEND.getLowerLimit()) {
            return Friendship.BEST_FRIEND.getFriendshipName();
        }
        if (pokeCount >= Friendship.NEW_FRIEND.getLowerLimit()) {
            return Friendship.NEW_FRIEND.getFriendshipName();
        }
        return Friendship.NON_FRIEND.getFriendshipName();
    }

    public List<Long> getMutualFriendIds(Long pokerId, Long pokedId) {
        List<Long> pokerFriendIds = friendRepository.findAllOfFriendIdsByUserId(pokerId);
        List<Long> pokedFriendIds = friendRepository.findAllOfFriendIdsByUserId(pokedId);
        return pokerFriendIds.stream()
                .filter(pokedFriendIds::contains)
                .toList();
    }

    public List<Long> getPokeFriendIdRandomly(Long userId) {
        val friendIdsPokeMe = friendRepository.findAllByFriendUserId(userId).stream()
                .map(Friend::getUserId)
                .toList();

        if (friendIdsPokeMe.isEmpty()) {
            return friendIdsPokeMe;
        }

        val friendIds = friendRepository.findAllByUserIdAndFriendUserIdIn(userId, friendIdsPokeMe).stream().map(
                Friend::getFriendUserId
        ).collect(Collectors.toList());

        if(friendIds.isEmpty()) {
            return friendIds;
        }

        List<Long> modifiableFriendIds = new ArrayList<>(friendIds);
        Collections.shuffle(modifiableFriendIds);
        return modifiableFriendIds.subList(0, 1);
    }

    public List<Long> findAllFriendIdsByUserIdRandomlyExcludeUserId(Long friendsUserId, List<Long> excludedUserId, int limitNum) {
        return friendRepository.getFriendRandom(friendsUserId, excludedUserId, limitNum);
    }

    public List<Long> findAllFriendIdsByUserId(Long userId) {
        return friendRepository.findAllOfFriendIdsByUserId(userId);
    }
}
