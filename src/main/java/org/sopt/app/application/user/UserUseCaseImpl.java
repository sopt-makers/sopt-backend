package org.sopt.app.application.user;

import lombok.RequiredArgsConstructor;
import org.sopt.app.application.user.dto.LogInUserDto;
import org.sopt.app.application.user.dto.SignUpUserDto;
import org.sopt.app.application.auth.EncryptService;
import org.sopt.app.application.user.service.UserService;
import org.sopt.app.common.exception.UserNotFoundException;
import org.sopt.app.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserUseCaseImpl implements UserUseCase {

    private final UserService userService;
    private final EncryptService encryptService;

    @Override
    public void signUp(SignUpUserDto userDto) {
        String encodedPassword = encryptService.encode(userDto.getPassword());
        userService.create(userDto, encodedPassword);
    }

    @Override
    public Long logIn(LogInUserDto userDto) {
        User user = userService.findUserByEmail(userDto.email());

        boolean matches = encryptService.matches(userDto.password(), user.getPassword());
        if (!matches) throw new UserNotFoundException("아이디/비밀번호를 확인해주세요.");

        return user.getId();
    }
}
