package org.sopt.app.application.rank;

import static org.sopt.app.common.ResponseCode.INVALID_RESPONSE;

import lombok.RequiredArgsConstructor;
import org.sopt.app.common.exception.ApiException;
import org.sopt.app.domain.entity.User;
import org.sopt.app.interfaces.postgres.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RankService {

  private final UserRepository userRepository;

  //User 한마디 등록하기
  public User updateProfileMessage(Long userId, String profileMessage){

    User user = userRepository.findUserById(userId)
        .orElseThrow(() -> new ApiException(INVALID_RESPONSE));

    user.updateProfileMessage(profileMessage);
    return userRepository.save(user);
  }

}
