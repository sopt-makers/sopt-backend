package org.sopt.app.application.user.service;

import lombok.RequiredArgsConstructor;
import org.sopt.app.application.user.dto.SignUpUserDto;
import org.sopt.app.common.exception.UserNotFoundException;
import org.sopt.app.domain.entity.User;
import org.sopt.app.interfaces.UserJpaRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserJpaRepository userRepository;

    public void create(SignUpUserDto userDto, String password) {
        userRepository.save(User.builder()
                .nickname(userDto.getNickname())
                .email(userDto.getEmail())
                .password(password)
                .osType(userDto.getOsType())
                .clientToken(userDto.getClientToken())
                .build());
    }

    public User findUserByEmail(String email) throws UserNotFoundException {
        // 가입된 사용자 아니면 튕겨내기.
        return userRepository.findUserByEmail(email).orElseThrow();
    }
}
