package org.sopt.app.application.poke;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.app.common.exception.NotFoundException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.Friend;
import org.sopt.app.domain.enums.Friendship;
import org.sopt.app.interfaces.postgres.FriendRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;

    public List<Long> findAllFriendIdsByUserIdRandomly(Long userId, int limitNum) {
        return friendRepository.getFriendRandom(userId, limitNum);
    }

    public List<Long> findAllFriendIdsByUserIdAndFriendship(Long userId, Integer lowerLimit, Integer upperLimit) {
        List<Long> targetFriendIds = friendRepository.findAllByUserIdAndPokeCountBetweenOrderByPokeCountDesc(
                userId, lowerLimit, upperLimit);
        return targetFriendIds.stream()
                .limit(2)
                .toList();
    }


    @Transactional
    public void createRelation(Long pokerId, Long pokedId) {
        Friend createdRelationUserToFriend = Friend.builder()
                .userId(pokerId)
                .friendUserId(pokedId)
                .pokeCount(1)
                .build();
        Friend createdRelationFriendToUser = Friend.builder()
                .userId(pokerId)
                .friendUserId(pokedId)
                .pokeCount(1)
                .build();
        friendRepository.save(createdRelationUserToFriend);
        friendRepository.save(createdRelationFriendToUser);
    }

    @Transactional
    public void applyPokeCount(Long pokerId, Long pokedId) {
        Friend friendship = friendRepository.findByUserIdAndFriendUserId(pokerId, pokedId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.FRIENDSHIP_NOT_FOUND.getMessage()));
        friendship.addPokeCount();
    }

    @Transactional(readOnly = true)
    public boolean isFriendEachOther(Long pokerId, Long pokedId) {
        Optional<Friend> pokerToPokedRelation = friendRepository.findByUserIdAndFriendUserId(pokerId, pokedId);
        Optional<Friend> pokedToPokerRelation = friendRepository.findByUserIdAndFriendUserId(pokedId, pokerId);
        return pokerToPokedRelation.isPresent() && pokedToPokerRelation.isPresent();
    }

    @Transactional(readOnly = true)
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
            return Friendship.SOULMATE.getValue();
        }
        if (pokeCount >= Friendship.BEST_FRIEND.getLowerLimit()) {
            return Friendship.BEST_FRIEND.getValue();
        }
        if (pokeCount >= Friendship.NEW_FRIEND.getLowerLimit()) {
            return Friendship.NEW_FRIEND.getValue();
        }
        return Friendship.NON_FRIEND.getValue();
    }

    @Transactional(readOnly = true)
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
