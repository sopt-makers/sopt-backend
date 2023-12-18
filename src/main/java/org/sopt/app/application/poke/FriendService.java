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
    public Page<Friend> findAllFriendsByFriendship(Long userId, Integer lowerLimit, Integer upperLimit, Pageable pageable) {
        return friendRepository.findAllByUserIdAndPokeCountBetweenOrderByPokeCountDesc(
                userId, lowerLimit, upperLimit, pageable);
    }


    public void createRelation(Long pokerId, Long pokedId) {
        Friend createdRelationUserToFriend = Friend.builder()
                .userId(pokerId)
                .friendUserId(pokedId)
                .pokeCount(0)
                .build();
        Friend createdRelationFriendToUser = Friend.builder()
                .userId(pokedId)
                .friendUserId(pokerId)
                .pokeCount(0)
                .build();
        friendRepository.save(createdRelationUserToFriend);
        friendRepository.save(createdRelationFriendToUser);
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
        Friend friendship = friendRepository.findByUserIdAndFriendUserId(pokerId, pokedId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.FRIENDSHIP_NOT_FOUND.getMessage()));
        return PokeInfo.Relationship.builder()
                .pokeCount(friendship.getPokeCount())
                .relationName(decideRelationName(friendship.getPokeCount()))
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

    public List<Long> getNotPokeFriendIdRandomly(Long userId, List<Long> pokedFriendIds, List<Long> pokeFriendIds) {
        List<Long> notPokeFriendIds = friendRepository.findAllByUserIdAndFriendUserIdNotInAndFriendUserIdNotIn(userId, pokedFriendIds, pokeFriendIds);
        Collections.shuffle(notPokeFriendIds);
        if (notPokeFriendIds.isEmpty()) {
            return List.of();
        }
        return notPokeFriendIds.subList(0, 1);
    }
}
