package org.sopt.app.application.user;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.auth.PlaygroundAuthInfo;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.exception.UnauthorizedException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.User;
import org.sopt.app.interfaces.postgres.UserRepository;
import org.sopt.app.presentation.auth.AuthRequest;
import org.sopt.app.presentation.user.UserRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserInfo.Id loginWithUserPlaygroundId(
            PlaygroundAuthInfo.PlaygroundMain playgroundMemberResponse,
            AuthRequest.CodeRequest codeRequest
    ) {
        val registeredUser = userRepository.findUserByPlaygroundId(playgroundMemberResponse.getId());

        if (registeredUser.isPresent()) {
            registeredUser.get().updatePlaygroundUserInfo(
                    playgroundMemberResponse.getName(),
                    playgroundMemberResponse.getAccessToken(),
                    codeRequest.getPushToken()
            );
            userRepository.save(registeredUser.get());

            return UserInfo.Id.builder().id(registeredUser.get().getId()).build();
        } else {
            val newUser = this.registerNewUser(
                    playgroundMemberResponse.getName(),
                    playgroundMemberResponse.getId(),
                    playgroundMemberResponse.getAccessToken(),
                    codeRequest.getPushToken()
            );
            userRepository.save(newUser);

            return UserInfo.Id.builder().id(newUser.getId()).build();
        }
    }

    private User registerNewUser(String username, Long playgroundId, String playgroundToken, String pushToken) {
        val nickname = this.generateNickname(username);
        return User.builder()
                .username(username)
                .nickname(nickname)
                .pushToken(pushToken)
                .playgroundId(playgroundId)
                .playgroundToken(playgroundToken)
                .points(0L)
                .allOptIn(false)
                .newsOptIn(false)
                .partOptIn(false)
                .pushToken("")
                .build();
    }

    private String generateNickname(String username) {
        return username + Math.round(Math.random() * 10000);
    }

    @Transactional(readOnly = true)
    public void checkUserNickname(String nickname) {
        val nicknameUser = userRepository.findUserByNickname(nickname);
        if (nicknameUser.isPresent()) {
            throw new BadRequestException(ErrorCode.DUPLICATE_NICKNAME.getMessage());
        }
    }

    @Transactional
    public UserInfo.Nickname editNickname(User user, String nickname) {
        user.editNickname(nickname);
        userRepository.save(user);
        return UserInfo.Nickname.builder().nickname(nickname).build();
    }

    @Transactional
    public UserInfo.ProfileMessage editProfileMessage(User user, String profileMessage) {
        user.updateProfileMessage(profileMessage);
        userRepository.save(user);
        return UserInfo.ProfileMessage.builder()
                .profileMessage(user.getProfileMessage())
                .build();
    }

    @Transactional
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public AuthRequest.AccessTokenRequest getPlaygroundToken(UserInfo.Id userId) {
        val user = userRepository.findUserById(userId.getId())
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.INVALID_REFRESH_TOKEN.getMessage()));
        val token = new AuthRequest.AccessTokenRequest();
        token.setAccessToken(user.getPlaygroundToken());
        return token;
    }

    @Transactional
    public void updatePushToken(User user, String pushToken) {
        user.updatePushToken(pushToken);
        userRepository.save(user);
    }

    @Transactional
    public User updateOptIn(User user, UserRequest.EditOptInRequest editOptInRequest) {
        if (editOptInRequest.getAllOptIn() != null) {
            user.updateAllOptIn(editOptInRequest.getAllOptIn());
        }
        if (editOptInRequest.getPartOptIn() != null) {
            user.updatePartOptIn(editOptInRequest.getPartOptIn());
        }
        if (editOptInRequest.getNewsOptIn() != null) {
            user.updateNewsOptIn(editOptInRequest.getNewsOptIn());
        }
        return userRepository.save(user);
    }
}
