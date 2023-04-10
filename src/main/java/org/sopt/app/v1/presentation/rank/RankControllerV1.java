package org.sopt.app.v1.presentation.rank;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.app.domain.entity.User;
import org.sopt.app.v1.application.rank.RankServiceV1;
import org.sopt.app.v1.presentation.BaseController;
import org.sopt.app.v1.presentation.rank.dto.FindAllRanksResponseDto;
import org.sopt.app.v1.presentation.rank.dto.FindRankResponseDto;
import org.sopt.app.v1.presentation.rank.dto.UserProfileRequestDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rank")
public class RankControllerV1 extends BaseController {

    private final RankServiceV1 rankServiceV1;

    /**
     * 한마디 편집하기
     */
    @PostMapping("/profileMessage")
    public User updateUserProfileMessage(
            @RequestHeader Long userId,
            @RequestBody UserProfileRequestDto userProfileRequestDto
    ) {

        return rankServiceV1
                .updateProfileMessage(userId, userProfileRequestDto.getProfileMessage());
    }

    @GetMapping("")
    public List<FindAllRanksResponseDto> findRanks() {
        return rankServiceV1.findRanks();
    }

    @GetMapping("/detail")
    public FindRankResponseDto findRankById(
            @RequestHeader Long userId) {
        return rankServiceV1.findRankById(userId);
    }
}
