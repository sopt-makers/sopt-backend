package org.sopt.app.application.user;

import static org.sopt.app.common.ResponseCode.ENTITY_NOT_FOUND;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.auth.PlaygroundAuthInfo;
import org.sopt.app.common.exception.EntityNotFoundException;
import org.sopt.app.common.exception.ExistUserException;
import org.sopt.app.domain.entity.User;
import org.sopt.app.interfaces.postgres.UserRepository;
import org.sopt.app.presentation.auth.AuthRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserInfo.Id loginWithUserPlaygroundId(PlaygroundAuthInfo.PlaygroundMain playgroundMemberResponse) {
        val registeredUser = userRepository.findUserByPlaygroundId(playgroundMemberResponse.getId());

        if (registeredUser.isPresent()) {
            registeredUser.get()
                    .updatePlaygroundUserInfo(playgroundMemberResponse.getName(),
                            playgroundMemberResponse.getAccessToken());
            userRepository.save(registeredUser.get());

            return UserInfo.Id.builder().id(registeredUser.get().getId()).build();
        } else {
            val newUser = this.registerNewUser(playgroundMemberResponse.getName(), playgroundMemberResponse.getId(),
                    playgroundMemberResponse.getAccessToken());
            userRepository.save(newUser);

            return UserInfo.Id.builder().id(newUser.getId()).build();
        }
    }

    private User registerNewUser(String username, Long playgroundId, String playgroundToken) {
        val nickname = this.generateNickname(username);
        return User.builder()
                .username(username)
                .nickname(nickname)
                .email("")
                .password("")
                .osType(null)
                .clientToken("")
                .playgroundId(playgroundId)
                .playgroundToken(playgroundToken)
                .points(0L)
                .build();
    }

    private String generateNickname(String username) {
        return username + Math.round(Math.random() * 10000);
    }

    @Transactional(readOnly = true)
    public void checkUserNickname(String nickname) {
        val nicknameUser = userRepository.findUserByNickname(nickname);
        if (nicknameUser.isPresent()) {
            throw new ExistUserException("사용 중인 닉네임입니다.");
        }
    }

    @Transactional
    public UserInfo.Nickname editNickname(User user, String nickname) {
        user.editNickname(nickname);
        userRepository.save(user);
        return UserInfo.Nickname.builder().nickname(nickname).build();
    }

    @Transactional
    public UserInfo.ProfileMessage editProfileMessage(Long userId, String profileMessage) {
        val user = userRepository.findUserById(userId).orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
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
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
        return AuthRequest.AccessTokenRequest.builder().accessToken(user.getPlaygroundToken()).build();
    }
}
