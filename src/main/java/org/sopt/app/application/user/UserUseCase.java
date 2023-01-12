package org.sopt.app.application.user;

import org.sopt.app.application.user.dto.LogInUserDto;
import org.sopt.app.application.user.dto.SignUpUserDto;
import org.sopt.app.domain.entity.User;

public interface UserUseCase {

    Long signUp(SignUpUserDto userDto);

    User logIn(LogInUserDto userDto);
}
