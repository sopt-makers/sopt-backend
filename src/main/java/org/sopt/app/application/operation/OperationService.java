package org.sopt.app.application.operation;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OperationService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${makers.operation.server}")
    private String baseURI;

    public OperationInfo.MainView getOperationForMainView(String accessToken) {
        val scoreResponse = this.getAttendanceScore(accessToken);
        return OperationInfo.MainView.builder()
                .announcement("공지다!")
                .attendanceScore(scoreResponse.getData().getScore())
                .build();
    }

    private OperationInfo.ScoreResponse getAttendanceScore(String accessToken) {
        val getScoreURL = baseURI + "/api/v1/app/score";

        val headers = new HttpHeaders();
        headers.add("content-type", "application/json;charset=UTF-8");
        headers.add("Authorization", accessToken);

        val entity = new HttpEntity(null, headers);

        val response = restTemplate.exchange(
                getScoreURL,
                HttpMethod.GET,
                entity,
                OperationInfo.ScoreResponse.class
        );
        return response.getBody();
    }
}
