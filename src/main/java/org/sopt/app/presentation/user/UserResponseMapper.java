package org.sopt.app.presentation.user;

import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sopt.app.application.auth.dto.PlaygroundProfileInfo;
import org.sopt.app.application.operation.OperationInfo;
import org.sopt.app.application.service.AppServiceInfo;
import org.sopt.app.application.user.UserInfo;
import org.sopt.app.domain.entity.User;
import org.sopt.app.presentation.user.UserResponse.AppService;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserResponseMapper {

    UserResponse.AppUser ofAppUser(User user);

    UserResponse.MainView ofMainView(PlaygroundProfileInfo.MainView user, OperationInfo.MainView operation,
            Boolean isAllConfirm);

    UserResponse.Nickname of(UserInfo.Nickname nickname);

    UserResponse.ProfileMessage of(UserInfo.ProfileMessage profileMessage);

    UserResponse.Generation ofGeneration(PlaygroundProfileInfo.UserActiveInfo userActiveInfo);

    List<AppService> ofAppServiceList(List<AppServiceInfo.AppService> appServiceList);
}
