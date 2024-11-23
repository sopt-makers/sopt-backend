package org.sopt.app.application.playground.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record PlayGroundCoffeeChatWrapper(
        @JsonProperty("coffeeChatList")
        List<PlayGroundCoffeeChatResponse> coffeeChatList
) {
}