package org.sopt.app.v1.application.user.service;

import lombok.RequiredArgsConstructor;
import org.sopt.app.common.exception.v1.UserNotFoundException;
import org.sopt.app.domain.entity.User;
import org.sopt.app.v1.application.user.dto.SignUpUserDto;
import org.sopt.app.v1.interfaces.postgres.UserRepositoryV1;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceV1 {

    private final UserRepositoryV1 userRepositoryV1;

    public Long create(SignUpUserDto userDto, String password) {
        User user = userRepositoryV1.save(User.builder()
                .username("")
                .nickname(userDto.getNickname())
                .email(userDto.getEmail())
                .password(password)
                .osType(null)
                .points(0L)
                .clientToken(userDto.getClientToken())
                .build());

        return user.getId();
    }

    public User findUserByEmail(String email) throws UserNotFoundException {
        return userRepositoryV1.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("아이디/비밀번호를 확인해주세요."));
    }
}
