package org.sopt.app.presentation.poke;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.ToString;
import org.sopt.app.application.poke.PokeInfo;

import java.util.List;

public class PokeResponse {

    @Getter
    @ToString
    public record PokeMessages(
            @Schema(description = "찌르기 메시지 리스트", example = "['메시지A', '메시지B', '메시지C', '메시지D', '메시지E']")
            List<String> messages
    ) {
    }

    @Getter
    @ToString
    public record SimplePokeProfile(
            @Schema(description = "유저 ID", example = "1")
            Long userId,
            @Schema(description = "프로필 사진 URL", example = ".....")
            String profileImage,
            @Schema(description = "유저 이름", example = "다혜다해")
            String name,
            @Schema(description = "메시지", example = "메시지A")
            String message,
            @Schema(description = "SOPT 최신 활동 기수 정보", example = "{'generation': 29, 'part': '안드로이드'}")
            PokeInfo.Activity activities,
            @Schema(description = "현재까지 찌른 횟수", example = "3")
            Long pickNum,
            @Schema(description = "함께 친구 관계인 친구들의 이름", example = "['제갈송현', '왕건모', '진동규', '차승호']")
            List<String> mutual,
            @Schema(description = "이전에 찌른 이력이 있는지에 대한 여부", example = "false")
            Boolean isFirstMeet,
            @Schema(description = "이미 오늘 찔렀는지에 대한 여부", example = "true")
            Boolean isAlreadyPoke
    ) {
    }

}

