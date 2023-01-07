package org.sopt.app.application.auth;

import lombok.RequiredArgsConstructor;
import org.sopt.app.common.exception.DbException;
import org.sopt.app.common.exception.ExistUserException;
import org.sopt.app.common.exception.UserNotFoundException;
import org.sopt.app.domain.entity.User;
import org.sopt.app.interfaces.postgres.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthUseCaseImpl {

    private final UserRepository userRepository;
    private final EncryptService encryptService;

    public void validate(String nickname, String email) {
        if (nickname != null) validateNickname(nickname);
        if (email != null) validateEmail(email);
    }

    @Transactional
    public void changePassword(String userId, String password) {
        User user = userRepository.findUserById(Long.parseLong(userId)).orElseThrow();
        user.password = encryptService.encode(password);
    }

    @Transactional
    public void changeNickname(String userId, String nickname) {
        User user = userRepository.findUserById(Long.parseLong(userId)).orElseThrow();
        user.nickname = nickname;
    }

    private void validateEmail(String email) throws UserNotFoundException {
        Optional<User> user = userRepository.findUserByEmail(email);
        if (user.isPresent()) throw new ExistUserException("이미 등록된 이메일입니다.");
    }

    private void validateNickname(String nickname) {
        Optional<User> user = userRepository.findUserByNickname(nickname);
        if (user.isPresent()) throw new ExistUserException("사용 중인 닉네임입니다.");

    }

    @Transactional
    public void deleteUser(String userId) {
        try {
            userRepository.deleteById(Long.parseLong(userId));
        } catch (Exception e) {
            throw new DbException(e);
        }
    }
}
