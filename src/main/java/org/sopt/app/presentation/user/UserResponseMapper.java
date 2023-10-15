package org.sopt.app.presentation.user;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sopt.app.application.auth.PlaygroundAuthInfo;
import org.sopt.app.application.operation.OperationInfo;
import org.sopt.app.application.user.UserInfo;
import org.sopt.app.domain.entity.User;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserResponseMapper {

    UserResponse.AppUser ofAppUser(User user);

    UserResponse.MainView ofMainView(PlaygroundAuthInfo.MainView user, OperationInfo.MainView operation,
            Boolean isAllConfirm);

    UserResponse.Nickname of(UserInfo.Nickname nickname);

    UserResponse.ProfileMessage of(UserInfo.ProfileMessage profileMessage);


    UserResponse.IsOptIn ofIsOptIn(User user);


    UserResponse.Generation ofGeneration(PlaygroundAuthInfo.UserActiveInfo userActiveInfo);

}
