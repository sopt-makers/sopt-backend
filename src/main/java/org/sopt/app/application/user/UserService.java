package org.sopt.app.application.user;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.auth.PlaygroundAuthInfo;
import org.sopt.app.common.exception.UnauthorizedException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.User;
import org.sopt.app.interfaces.postgres.UserRepository;
import org.sopt.app.presentation.auth.AppAuthRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserInfo.Id loginWithUserPlaygroundId(
            PlaygroundAuthInfo.PlaygroundMain playgroundMemberResponse
    ) {
        val registeredUser = userRepository.findUserByPlaygroundId(playgroundMemberResponse.getId());

        // 기존에 로그인/가입한 이력이 있으면
        if (registeredUser.isPresent()) {
            registeredUser.get().updatePlaygroundUserInfo(
                    playgroundMemberResponse.getName(),
                    playgroundMemberResponse.getAccessToken()
            );
            userRepository.save(registeredUser.get());

            return UserInfo.Id.builder()
                    .id(registeredUser.get().getId()).build();
        } else {
            val newUser = this.registerNewUser(
                    playgroundMemberResponse.getName(),
                    playgroundMemberResponse.getId(),
                    playgroundMemberResponse.getAccessToken()
            );
            userRepository.save(newUser);

            return UserInfo.Id.builder()
                    .id(newUser.getId()).build();
        }
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
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.INVALID_REFRESH_TOKEN.getMessage()));
        val token = new AppAuthRequest.AccessTokenRequest();
        token.setAccessToken(user.getPlaygroundToken());
        return token;
    }

    @Transactional
    public void updatePlaygroundToken(UserInfo.Id userId, String playgroundToken) {
        val user = userRepository.findUserById(userId.getId())
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.INVALID_REFRESH_TOKEN.getMessage()));
        val newUser = User.builder()
                .id(user.getId())
                .username(user.getUsername())
                .playgroundId(user.getPlaygroundId())
                .playgroundToken(playgroundToken)
                .build();
        userRepository.save(newUser);
    }

    public List<UserInfo.UserProfile> getUserProfiles(List<Long> recommendUserIds) {
        return userRepository.findAllByPlaygroundIdIn(recommendUserIds).stream().map(
            u -> UserInfo.UserProfile.builder()
                .userId(u.getId())
                .name(u.getUsername())
                .playgroundId(u.getPlaygroundId())
                .build()
        ).toList();
    }

    public List<UserInfo.PorkProfile> combinePokeProfileList(
            List<UserInfo.UserProfile> userProfiles, List<PlaygroundAuthInfo.PlaygroundProfileWithId> playgroundProfiles
    ) {
        return userProfiles.stream().map(userProfile -> {
            val playgroundProfile = playgroundProfiles.stream()
                .filter(profile -> profile.getId().equals(userProfile.getPlaygroundId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("플레이그라운드 프로필이 없습니다."));
            val generation = playgroundProfile.getActivities().get(0).getCardinalActivities().get(0)
                .getGeneration();
            val part = playgroundProfile.getActivities().get(0).getCardinalActivities().get(0)
                .getPart();
            return UserInfo.PorkProfile.builder()
                .userId(userProfile.getUserId())
                .profileImage(playgroundProfile.getProfileImage())
                .name(playgroundProfile.getName())
                .generation(generation)
                .part(part)
                .build();
        }).toList();
    }
}
