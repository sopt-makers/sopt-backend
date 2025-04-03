package org.sopt.app.application.user;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.LoginInfo;
import org.sopt.app.common.exception.NotFoundException;
import org.sopt.app.common.exception.UnauthorizedException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.Icons;
import org.sopt.app.domain.entity.User;
import org.sopt.app.domain.enums.IconType;
import org.sopt.app.interfaces.postgres.IconRepository;
import org.sopt.app.interfaces.postgres.UserRepository;
import org.sopt.app.presentation.auth.AppAuthRequest.AccessTokenRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final IconRepository iconRepository;

    @Transactional
    public Long upsertUser(LoginInfo loginInfo) {
        Optional<User> user = userRepository.findUserByPlaygroundId(loginInfo.playgroundId());

        if (user.isPresent()) {
            return this.updateRegisteredUserInfo(user.get(), loginInfo).getId();
        }
        return this.registerNewUser(loginInfo).getId();
    }

    private User updateRegisteredUserInfo(User registeredUser, LoginInfo loginInfo) {
        registeredUser.updatePlaygroundUserInfo(loginInfo.name(), loginInfo.playgroundToken());
        return registeredUser;
    }

    private User registerNewUser(LoginInfo loginInfo) {
        User newUser = User.builder()
                .username(loginInfo.name())
                .playgroundId(loginInfo.playgroundId())
                .playgroundToken(loginInfo.playgroundToken())
                .build();
        return userRepository.save(newUser);
    }

    public AccessTokenRequest getPlaygroundToken(Long userId) {
        val user = userRepository.findUserById(userId)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.INVALID_REFRESH_TOKEN));
        return new AccessTokenRequest(user.getPlaygroundToken());
    }

    @Transactional
    public void updatePlaygroundToken(Long userId, String playgroundToken) {
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.INVALID_REFRESH_TOKEN));
        user.updatePlaygroundToken(playgroundToken);
    }

    public UserProfile getUserProfileOrElseThrow(Long userId) {
        val user = userRepository.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        return UserProfile.of(user);
    }

    public List<String> getNamesByIds(List<Long> userIds) {
        return userRepository.findAllByIdIn(userIds).stream()
                .map(User::getUsername)
                .toList();
    }

    public List<UserProfile> getUserProfilesByPlaygroundIds(List<Long> playgroundIds) {
        return userRepository.findAllByPlaygroundIdIn(playgroundIds).stream().map(UserProfile::of).toList();
    }

    public List<Long> getAllPlaygroundIds() {
        return userRepository.findAllPlaygroundId();
    }

    public boolean isUserExist(Long userId) {
        return userRepository.existsById(userId);
    }

    public List<String> getIcons(IconType iconType) {
        return iconRepository.findAllByIconType(iconType).stream()
                .map(Icons::getIconUrl)
                .toList();
    }
}
