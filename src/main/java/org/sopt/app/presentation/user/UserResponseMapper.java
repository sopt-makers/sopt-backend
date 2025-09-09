package org.sopt.app.presentation.user;

import org.mapstruct.*;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo;
import org.sopt.app.application.user.UserInfo;
import org.sopt.app.presentation.user.UserResponse.*;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserResponseMapper {

    UserResponse.MainView ofMainView(PlaygroundProfileInfo.MainView user, UserResponse.Operation operation,
            Boolean isAllConfirm);

    UserResponse.ProfileMessage of(ProfileMessage profileMessage);

    UserResponse.Generation ofGeneration(PlaygroundProfileInfo.UserActiveInfo userActiveInfo);

    Create ofCreate(UserInfo userInfo);
}
