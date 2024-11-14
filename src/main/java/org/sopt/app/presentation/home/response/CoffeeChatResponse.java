package org.sopt.app.presentation.home.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.sopt.app.application.playground.dto.PlayGroundCoffeeChatResponse;

@Getter
@Builder
public class CoffeeChatResponse {
    private Long memberId;
    private String bio;
    private List<String> topicTypeList;
    private String profileImage;
    private String name;
    private String career;
    private String organization;
    private String companyJob;
    private List<String> soptActivities;
    private boolean isMine;
    private boolean isBlind;

    public static CoffeeChatResponse of(PlayGroundCoffeeChatResponse playGroundCoffeeChatResponse){
        return CoffeeChatResponse.builder()
                .memberId(playGroundCoffeeChatResponse.memberId())
                .bio(playGroundCoffeeChatResponse.bio())
                .topicTypeList(playGroundCoffeeChatResponse.topicTypeList())
                .profileImage(playGroundCoffeeChatResponse.profileImage())
                .name(playGroundCoffeeChatResponse.name())
                .career(playGroundCoffeeChatResponse.career())
                .organization(playGroundCoffeeChatResponse.organization())
                .companyJob(playGroundCoffeeChatResponse.companyJob())
                .soptActivities(playGroundCoffeeChatResponse.soptActivities())
                .build();
    }
}