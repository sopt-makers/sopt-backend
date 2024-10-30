package org.sopt.app.application.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.app.common.event.Event;

@Getter
@RequiredArgsConstructor
public class UserWithdrawEvent extends Event {

    private final Long userId;
}
