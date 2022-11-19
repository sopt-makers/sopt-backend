package org.sopt.app.application.user;

import org.sopt.app.application.user.dto.SignUpUserDto;

public interface UserUseCase {

    void signUp(SignUpUserDto userDto);
}
