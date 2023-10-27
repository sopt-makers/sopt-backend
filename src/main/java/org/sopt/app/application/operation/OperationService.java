package org.sopt.app.application.operation;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.interfaces.external.OperationClient;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException.BadRequest;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OperationService {

    private final OperationClient operationClient;

    public OperationInfo.MainView getOperationForMainView(String accessToken) {
        val scoreResponse = this.getAttendanceScore(accessToken);
        return OperationInfo.MainView.builder()
                .announcement("공지다!")
                .attendanceScore(scoreResponse.getData().getScore())
                .build();
    }

    private OperationInfo.ScoreResponse getAttendanceScore(String accessToken) {
        Map<String, String> headers = Map.of(
                "content-type", "application/json;charset=UTF-8",
                "Authorization", accessToken
        );
        try {
            return operationClient.getScore(headers);
        } catch (BadRequest e) {
            throw new BadRequestException(ErrorCode.OPERATION_PROFILE_NOT_EXISTS.getMessage());
        }
    }
}
