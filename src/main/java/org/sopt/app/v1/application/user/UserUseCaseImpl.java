package org.sopt.app.v1.application.user;

import lombok.RequiredArgsConstructor;
import org.sopt.app.common.exception.UserNotFoundException;
import org.sopt.app.domain.entity.User;
import org.sopt.app.v1.application.auth.EncryptService;
import org.sopt.app.v1.application.user.dto.LogInUserDto;
import org.sopt.app.v1.application.user.dto.SignUpUserDto;
import org.sopt.app.v1.application.user.service.UserServiceV1;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserUseCaseImpl implements UserUseCase {

    private final UserServiceV1 userServiceV1;
    private final EncryptService encryptService;

    @Override
    public Long signUp(SignUpUserDto userDto) {
        String encodedPassword = encryptService.encode(userDto.getPassword());
        return userServiceV1.create(userDto, encodedPassword);
    }

    @Override
    public User logIn(LogInUserDto userDto) {
        User user = userServiceV1.findUserByEmail(userDto.email());

        boolean matches = encryptService.matches(userDto.password(), user.getPassword());
        if (!matches) {
            throw new UserNotFoundException("아이디/비밀번호를 확인해주세요.");
        }

        return user;
    }
}
