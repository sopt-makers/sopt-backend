package org.sopt.app.application.user;

import lombok.RequiredArgsConstructor;
import org.sopt.app.application.user.dto.LogInUserDto;
import org.sopt.app.application.user.dto.SignUpUserDto;
import org.sopt.app.application.user.service.EncryptService;
import org.sopt.app.application.user.service.UserService;
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
        User user = userService.findUserByEmail(userDto.getEmail());

        Boolean matches = encryptService.matches(userDto.getPassword(),user.getPassword());
        if (!matches) throw new RuntimeException("비밀번호가 틀립니다.");

        return user.getId();
    }
}
