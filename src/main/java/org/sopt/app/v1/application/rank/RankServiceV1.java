package org.sopt.app.v1.application.rank;

import static org.sopt.app.common.ResponseCode.INVALID_RESPONSE;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.app.common.exception.ApiException;
import org.sopt.app.domain.entity.User;
import org.sopt.app.v1.application.mission.MissionServiceV1;
import org.sopt.app.v1.interfaces.postgres.UserRepositoryV1;
import org.sopt.app.v1.presentation.rank.dto.FindAllRanksResponseDto;
import org.sopt.app.v1.presentation.rank.dto.FindRankResponseDto;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RankServiceV1 {

    private final UserRepositoryV1 userRepositoryV1;

    private final MissionServiceV1 missionServiceV1;

    //User 한마디 등록하기
    public User updateProfileMessage(Long userId, String profileMessage) {

        User user = userRepositoryV1.findUserById(userId)
                .orElseThrow(() -> new ApiException(INVALID_RESPONSE));

        user.updateProfileMessage(profileMessage);
        return userRepositoryV1.save(user);
    }

    public List<FindAllRanksResponseDto> findRanks() {
        List<User> users = userRepositoryV1.findAll();
        AtomicInteger rankPoint = new AtomicInteger(1);
        return users.stream().sorted(
                        Comparator.comparing(User::getPoints).reversed())
                .map(user -> FindAllRanksResponseDto.builder()
                        .rank(rankPoint.getAndIncrement())
                        .userId(user.getId())
                        .nickname(user.getNickname())
                        .point(user.getPoints())
                        .profileMessage(user.getProfileMessage())
                        .build())
                .collect(Collectors.toList());
    }

    public FindRankResponseDto findRankById(Long userId) {
        User user = userRepositoryV1.findUserById(userId).orElseThrow(() -> new ApiException(INVALID_RESPONSE));
        return FindRankResponseDto.builder()
                .userId(userId)
                .nickname(user.getNickname())
                .profileMessage(user.getProfileMessage())
                .userMissions(missionServiceV1.getCompleteMission(String.valueOf(userId)))
                .build();
    }

}
