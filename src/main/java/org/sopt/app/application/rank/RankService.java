package org.sopt.app.application.rank;

import lombok.RequiredArgsConstructor;
import org.sopt.app.common.exception.ApiException;
import org.sopt.app.domain.entity.User;
import org.sopt.app.interfaces.postgres.UserRepository;
import org.sopt.app.presentation.rank.dto.RankResponseDto;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.sopt.app.common.ResponseCode.INVALID_RESPONSE;

@Service
@RequiredArgsConstructor
public class RankService {

    private final UserRepository userRepository;

    //User 한마디 등록하기
    public User updateProfileMessage(Long userId, String profileMessage) {

        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new ApiException(INVALID_RESPONSE));

        user.updateProfileMessage(profileMessage);
        return userRepository.save(user);
    }

    public List<RankResponseDto> findRanks(){
      List<User> users = userRepository.findAll();
      AtomicInteger rankPoint = new AtomicInteger(1);
      return users.stream().sorted(
              Comparator.comparing(User::getPoints).reversed())
              .map(user -> RankResponseDto.builder()
                      .rank(rankPoint.getAndIncrement())
                      .nickname(user.getNickname())
                      .point(user.getPoints())
                      .profileMessage(user.getProfileMessage())
                      .build())
              .collect(Collectors.toList());
    }

}
