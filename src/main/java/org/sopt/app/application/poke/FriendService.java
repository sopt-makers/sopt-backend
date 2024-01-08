package org.sopt.app.application.poke;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
        return friendRepository.findAllByUserIdAndPokeCountBetweenOrderByPokeCountDesc(
                userId, lowerLimit, upperLimit);
    }

    public int findAllFriendSizeByFriendship(Long userId, Integer lowerLimit, Integer upperLimit) {
        return friendRepository.findSizeByUserIdAndPokeCountBetween(
            userId, lowerLimit, upperLimit);
    }

    public Page<Friend> findAllFriendsByFriendship(Long userId, Integer lowerLimit, Integer upperLimit, Pageable pageable) {
        return friendRepository.findAllByUserIdAndPokeCountBetweenOrderByPokeCountDesc(
                userId, lowerLimit, upperLimit, pageable);
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
        List<Long> pokeFriendIds = friendRepository.findAllOfFriendIdsByUserId(userId);
        Collections.shuffle(pokeFriendIds);
        return pokeFriendIds.subList(0, 1);
    }

    public List<Long> findAllFriendIdsByUserIdRandomlyExcludeUserId(Long friendsUserId, List<Long> excludedUserId, int limitNum) {
        return friendRepository.getFriendRandom(friendsUserId, excludedUserId, limitNum);
    }

    public List<Long> findAllFriendIdsByUserId(Long userId) {
        return friendRepository.findAllOfFriendIdsByUserId(userId);
    }
}
