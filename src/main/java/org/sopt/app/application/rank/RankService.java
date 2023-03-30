package org.sopt.app.application.rank;

import static org.sopt.app.common.ResponseCode.ENTITY_NOT_FOUND;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.common.exception.EntityNotFoundException;
import org.sopt.app.domain.entity.User;
import org.sopt.app.interfaces.postgres.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RankService {

    private final UserRepository userRepository;

    public List<RankInfo.Main> findRanks() {
        val userList = userRepository.findAll();
        val rankPoint = new AtomicInteger(1);
        return userList.stream().sorted(
                        Comparator.comparing(User::getPoints).reversed())
                .map(user -> RankInfo.Main.builder()
                        .rank(rankPoint.getAndIncrement())
                        .nickname(user.getNickname())
                        .point(user.getPoints())
                        .profileMessage(user.getProfileMessage())
                        .build())
                .collect(Collectors.toList());
    }

    public User findRankByNickname(String nickname) {
        return userRepository.findUserByNickname(nickname)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
    }
}
