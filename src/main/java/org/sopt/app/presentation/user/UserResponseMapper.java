package org.sopt.app.presentation.user;

import java.util.List;
import org.mapstruct.*;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo;
import org.sopt.app.application.operation.OperationInfo;
import org.sopt.app.application.app_service.AppServiceInfo;
import org.sopt.app.presentation.user.UserResponse.*;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserResponseMapper {

    UserResponse.MainView ofMainView(PlaygroundProfileInfo.MainView user, OperationInfo.MainView operation,
            Boolean isAllConfirm);

    UserResponse.Nickname of(Nickname nickname);

    UserResponse.ProfileMessage of(ProfileMessage profileMessage);

    UserResponse.Generation ofGeneration(PlaygroundProfileInfo.UserActiveInfo userActiveInfo);

    List<AppService> ofAppServiceList(List<AppServiceInfo.AppService> appServiceList);
}
