package org.sopt.app.application.user;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo;
import org.sopt.app.common.exception.NotFoundException;
import org.sopt.app.common.exception.UnauthorizedException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.User;
import org.sopt.app.interfaces.postgres.UserRepository;
import org.sopt.app.presentation.auth.AppAuthRequest.AccessTokenRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public Long loginWithUserPlaygroundId(
            PlaygroundProfileInfo.PlaygroundMain playgroundMemberResponse
    ) {
        val registeredUser = userRepository.findUserByPlaygroundId(
                playgroundMemberResponse.getId());

        // 기존에 로그인/가입한 이력이 있으면
        if (registeredUser.isPresent()) {
            registeredUser.get().updatePlaygroundUserInfo(
                    playgroundMemberResponse.getName(),
                    playgroundMemberResponse.getAccessToken()
            );
            userRepository.save(registeredUser.get());

            return registeredUser.get().getId();
        }

        val newUser = userRepository.save(
                this.registerNewUser(
                        playgroundMemberResponse.getName(),
                        playgroundMemberResponse.getId(),
                        playgroundMemberResponse.getAccessToken()
                ));

        return newUser.getId();
    }

    private User registerNewUser(String username, Long playgroundId, String playgroundToken) {
        return User.builder()
            .username(username)
            .playgroundId(playgroundId)
            .playgroundToken(playgroundToken)
            .build();
    }

    @Transactional
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public AccessTokenRequest getPlaygroundToken(Long userId) {
        val user = userRepository.findUserById(userId)
            .orElseThrow(() -> new UnauthorizedException(ErrorCode.INVALID_REFRESH_TOKEN));
        return new AccessTokenRequest(user.getPlaygroundToken());
    }

    @Transactional
    public void updatePlaygroundToken(Long userId, String playgroundToken) {
        val user = userRepository.findUserById(userId)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.INVALID_REFRESH_TOKEN));
        userRepository.save(
                User.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .playgroundId(user.getPlaygroundId())
                        .playgroundToken(playgroundToken)
                        .build()
        );
    }

    public UserProfile getUserProfileOrElseThrow(Long userId) {
        val user = userRepository.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        return UserProfile.builder()
                        .userId(user.getId())
                        .name(user.getUsername())
                        .playgroundId(user.getPlaygroundId())
                        .build();
    }

    public List<String> getNamesByIds(List<Long> userIds) {
        return userRepository.findAllByIdIn(userIds).stream()
                .map(User::getUsername)
                .toList();
    }

    public List<UserProfile> getUserProfilesByPlaygroundIds(List<Long> playgroundIds) {
        return userRepository.findAllByPlaygroundIdIn(playgroundIds).stream().map(
                user -> UserProfile.builder()
                        .userId(user.getId())
                        .name(user.getUsername())
                        .playgroundId(user.getPlaygroundId())
                        .build()
        ).toList();
    }

    public List<Long> getAllPlaygroundIds() {
        return userRepository.findAllPlaygroundId();
    }

    public boolean isUserExist(Long userId) {
        return userRepository.existsById(userId);
    }
}
