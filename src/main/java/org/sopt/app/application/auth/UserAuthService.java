package org.sopt.app.application.auth;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.user.UserInfo;
import org.sopt.app.application.user.UserInfoMapper;
import org.sopt.app.domain.entity.User;
import org.sopt.app.interfaces.postgres.UserRepository;
import org.sopt.app.presentation.auth.AuthResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final UserRepository userRepository;
    private final UserInfoMapper userInfoMapper;

    public UserInfo.Id loginWithUserPlaygroundId(AuthResponse.PlaygroundMemberResponse playgroundMemberResponse) {
        Optional<User> registeredUser = userRepository.findUserByPlaygroundId(playgroundMemberResponse.getId());

        UserInfo.Id userId;
        if (registeredUser.isPresent()) {
            userId = userInfoMapper.of(registeredUser.get().getId());
        } else {
            User newUser = User.builder()
                    .nickname(playgroundMemberResponse.getName())
                    .email("")
                    .password("")
                    .osType(null)
                    .clientToken("")
                    .playgroundId(playgroundMemberResponse.getId())
                    .build();
            userRepository.save(newUser);

            userId = userInfoMapper.of(newUser.getId());
        }

        return userId;
    }
}
