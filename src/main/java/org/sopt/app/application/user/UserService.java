package org.sopt.app.application.user;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.app.domain.entity.User;
import org.sopt.app.interfaces.postgres.UserRepository;
import org.sopt.app.presentation.auth.AuthRequest;
import org.sopt.app.presentation.auth.AuthResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserInfo.Id loginWithUserPlaygroundId(AuthResponse.PlaygroundMemberResponse playgroundMemberResponse,
            AuthRequest.AccessTokenRequest playgroundToken) {
        Optional<User> registeredUser = userRepository.findUserByPlaygroundId(playgroundMemberResponse.getId());

        UserInfo.Id userId;
        if (registeredUser.isPresent()) {
            registeredUser.get()
                    .updatePlaygroundUserInfo(playgroundMemberResponse.getName(), playgroundToken.getAccessToken());
            userRepository.save(registeredUser.get());

            userId = UserInfo.Id.builder().id(registeredUser.get().getId()).build();
        } else {
            int randomNumber = (int) (Math.random() * 10000);
            User newUser = User.builder()
                    .username(playgroundMemberResponse.getName())
                    .nickname(playgroundMemberResponse.getName() + randomNumber)
                    .email("")
                    .password("")
                    .osType(null)
                    .clientToken("")
                    .playgroundId(playgroundMemberResponse.getId())
                    .playgroundToken(playgroundToken.getAccessToken())
                    .build();
            userRepository.save(newUser);

            userId = UserInfo.Id.builder().id(newUser.getId()).build();
        }

        return userId;
    }
}
