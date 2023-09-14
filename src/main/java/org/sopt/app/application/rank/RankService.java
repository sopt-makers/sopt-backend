package org.sopt.app.application.rank;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.auth.PlaygroundAuthInfo.ActiveUserIds;
import org.sopt.app.application.rank.RankInfo.Main;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.User;
import org.sopt.app.interfaces.postgres.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RankService {

    private final UserRepository userRepository;

    public List<RankInfo.Main> findRanks() {
        val userList = userRepository.findAll();
        return this.getRanking(userList);
    }

    public User findRankByNickname(String nickname) {
        return userRepository.findUserByNickname(nickname)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND.getMessage()));
    }

    public List<RankInfo.Main> findCurrentRanks(ActiveUserIds activeUserIds) {
        val userList = userRepository.findAllByPlaygroundIdIn(activeUserIds.getMemberIds());
        return this.getRanking(userList);
    }

    private List<Main> getRanking(List<User> userList) {
        val rankPoint = new AtomicInteger(1);
        return userList.stream().sorted(
                Comparator.comparing(User::getPoints).reversed())
            .map(user -> Main.builder()
                .rank(rankPoint.getAndIncrement())
                .nickname(user.getNickname())
                .point(user.getPoints())
                .profileMessage(user.getProfileMessage())
                .build())
            .collect(Collectors.toList());
    }

}
