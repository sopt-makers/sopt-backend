package org.sopt.app.presentation.rank;

import lombok.RequiredArgsConstructor;
import org.sopt.app.application.rank.RankService;
import org.sopt.app.domain.entity.User;
import org.sopt.app.presentation.BaseController;
import org.sopt.app.presentation.rank.dto.RankResponseDto;
import org.sopt.app.presentation.rank.dto.UserProfileRequestDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rank")
public class RankController extends BaseController {

    private final RankService rankService;

    /**
     * 한마디 편집하기
     */
    @PostMapping("/profileMessage")
    public User updateUserProfileMessage(
            @RequestHeader Long userId,
            @RequestBody UserProfileRequestDto userProfileRequestDto
    ) {

        return rankService
                .updateProfileMessage(userId, userProfileRequestDto.getProfileMessage());
    }

    @GetMapping("")
    public List<RankResponseDto> findRanks() {
        return rankService.findRanks();
    }
}
