package org.sopt.app.application.notification;

import lombok.RequiredArgsConstructor;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.User;
import org.sopt.app.domain.entity.UserNotificationOption;
import org.sopt.app.interfaces.postgres.UserNotificationOptionRepository;
import org.sopt.app.interfaces.postgres.UserRepository;
import org.sopt.app.presentation.notification.OptionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationOptionService {

    private final UserRepository userRepository;
    private final UserNotificationOptionRepository notificationOptionRepository;

    @Transactional
    public void registerOptIn(Long userId) {
        User registerUser = userRepository.findUserById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND.getMessage()));

        notificationOptionRepository.save(UserNotificationOption.builder()
                        .user(registerUser)
//                        .allOptIn(false)
//                        .partOptIn(false)
//                        .newsOptIn(false)
                .build()
        );
//        UserNotificationOption registerOption = notificationOptionRepository.findByUser(registerUser).get();
//        registerUser.updateNotificationOption(save);
//        userRepository.save(registerUser);
    }

    @Transactional(readOnly = true)
    public UserNotificationOption getOption(User user) {
        return notificationOptionRepository.findByUser(user)
                .orElseThrow(() -> new BadRequestException(ErrorCode.TARGET_OPTION_NOT_FOUND.getMessage()));
    }

    @Transactional
    public UserNotificationOption updateOptIn(UserNotificationOption options, OptionRequest.EditOptInRequest editOptInRequest) {
        if (editOptInRequest.getAllOptIn() != null) {
            options.updateAllOptIn(editOptInRequest.getAllOptIn());
        }
        if (editOptInRequest.getPartOptIn() != null) {
            options.updatePartOptIn(editOptInRequest.getPartOptIn());
        }
        if (editOptInRequest.getNewsOptIn() != null) {
            options.updateNewsOptIn(editOptInRequest.getNewsOptIn());
        }
        return notificationOptionRepository.save(options);
    }
}
