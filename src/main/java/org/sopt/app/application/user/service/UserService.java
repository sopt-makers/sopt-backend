package org.sopt.app.application.user.service;

import lombok.RequiredArgsConstructor;
import org.sopt.app.application.user.dto.SignUpUserDto;
import org.sopt.app.common.exception.UserNotFoundException;
import org.sopt.app.domain.entity.User;
import org.sopt.app.interfaces.postgres.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Long create(SignUpUserDto userDto, String password) {
        User user = userRepository.save(User.builder()
            .nickname(userDto.getNickname())
            .email(userDto.getEmail())
            .password(password)
            .osType(userDto.getOsType())
            .points(0L)
            .clientToken(userDto.getClientToken())
            .build());

        return user.getId();
    }

    public User findUserByEmail(String email) throws UserNotFoundException {
        return userRepository.findUserByEmail(email).orElseThrow(() -> new UserNotFoundException("아이디/비밀번호를 확인해주세요."));
    }
}
