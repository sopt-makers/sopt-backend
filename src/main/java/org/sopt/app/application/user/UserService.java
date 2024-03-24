package org.sopt.app.application.user;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.auth.PlaygroundAuthInfo;
import org.sopt.app.application.user.UserInfo.UserProfile;
import org.sopt.app.common.exception.UnauthorizedException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.User;
import org.sopt.app.interfaces.postgres.UserRepository;
import org.sopt.app.presentation.auth.AppAuthRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserInfo.Id loginWithUserPlaygroundId(
            PlaygroundAuthInfo.PlaygroundMain playgroundMemberResponse
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

            return UserInfo.Id.builder()
                    .id(registeredUser.get().getId()).build();
        }

        val newUser = userRepository.save(
                this.registerNewUser(
                        playgroundMemberResponse.getName(),
                        playgroundMemberResponse.getId(),
                        playgroundMemberResponse.getAccessToken()
                ));

        return UserInfo.Id.builder()
                .id(newUser.getId()).build();
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
    public AppAuthRequest.AccessTokenRequest getPlaygroundToken(UserInfo.Id userId) {
        val user = userRepository.findUserById(userId.getId())
            .orElseThrow(
                () -> new UnauthorizedException(ErrorCode.INVALID_REFRESH_TOKEN.getMessage()));
        val token = new AppAuthRequest.AccessTokenRequest();
        token.setAccessToken(user.getPlaygroundToken());
        return token;
    }

    @Transactional
    public void updatePlaygroundToken(UserInfo.Id userId, String playgroundToken) {
        val user = userRepository.findUserById(userId.getId())
            .orElseThrow(
                () -> new UnauthorizedException(ErrorCode.INVALID_REFRESH_TOKEN.getMessage()));
        val newUser = User.builder()
            .id(user.getId())
            .username(user.getUsername())
            .playgroundId(user.getPlaygroundId())
            .playgroundToken(playgroundToken)
            .build();
        userRepository.save(newUser);
    }

    @Transactional(readOnly = true)
    public UserProfile getUserProfile(Long userId) {
        val user = userRepository.findUserById(userId)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.USER_NOT_FOUND.getMessage()));
        return UserProfile.builder()
                        .userId(user.getId())
                        .name(user.getUsername())
                        .playgroundId(user.getPlaygroundId())
                        .build();
    }

    public List<UserProfile> getUserProfilesByPlaygroundIds(List<Long> playgroundIds) {
        return userRepository.findAllByPlaygroundIdIn(playgroundIds).stream().map(
            u -> UserProfile.builder()
                .userId(u.getId())
                .name(u.getUsername())
                .playgroundId(u.getPlaygroundId())
                .build()
        ).toList();
    }

    public List<UserProfile> getUserProfileByUserId(List<Long> userId) {
        return userRepository.findAllByIdIn(userId).stream().map(
            u -> UserProfile.builder()
                .userId(u.getId())
                .name(u.getUsername())
                .playgroundId(u.getPlaygroundId())
                .build()
        ).collect(Collectors.toList());
    }
}
