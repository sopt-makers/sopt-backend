package org.sopt.app.v1.application.user;

import org.sopt.app.domain.entity.User;
import org.sopt.app.v1.application.user.dto.LogInUserDto;
import org.sopt.app.v1.application.user.dto.SignUpUserDto;

public interface UserUseCase {

    Long signUp(SignUpUserDto userDto);

    User logIn(LogInUserDto userDto);
}
