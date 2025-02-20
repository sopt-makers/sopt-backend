package org.sopt.app.presentation.home.response;

import lombok.*;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class HomeDescriptionResponse {
    private final String activityDescription;
    public static HomeDescriptionResponse of(String userName, int totalActivityMonths) {
        return new HomeDescriptionResponse(
                userName + "님은<br>SOPT와 " + totalActivityMonths + "개월째"
        );
    }
}
