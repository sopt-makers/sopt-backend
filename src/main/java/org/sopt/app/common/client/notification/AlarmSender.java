package org.sopt.app.common.client.notification;

import org.sopt.app.common.client.notification.dto.request.AlarmRequest;
import org.sopt.app.common.client.notification.dto.response.AlarmResponse;

public interface AlarmSender <T extends AlarmRequest>{
    AlarmResponse send(T request);
}
