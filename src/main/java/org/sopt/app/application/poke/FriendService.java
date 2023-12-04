package org.sopt.app.application.poke;

import lombok.RequiredArgsConstructor;
import org.sopt.app.common.exception.NotFoundException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.Friend;
import org.sopt.app.interfaces.postgres.FriendRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;

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
        Friend friendship = friendRepository.findByUserIdAndAndFriendUserId(pokerId, pokedId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.FRIENDSHIP_NOT_FOUND.getMessage()));
        friendship.addPokeCount();
    }

    @Transactional(readOnly = true)
    public boolean isFriendEachOther(Long pokerId, Long pokedId) {
        Optional<Friend> pokerToPokedRelation = friendRepository.findByUserIdAndAndFriendUserId(pokerId, pokedId);
        Optional<Friend> pokedToPokerRelation = friendRepository.findByUserIdAndAndFriendUserId(pokedId, pokerId);
        return pokerToPokedRelation.isPresent() && pokedToPokerRelation.isPresent();
    }

}
