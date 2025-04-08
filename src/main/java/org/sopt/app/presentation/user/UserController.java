package org.sopt.app.presentation.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.sopt.app.application.fortune.FortuneService;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.PlaygroundProfile;
import org.sopt.app.application.soptamp.SoptampUserService;
import org.sopt.app.common.utils.ActivityDurationCalculator;
import org.sopt.app.domain.entity.User;
import org.sopt.app.domain.enums.IconType;
import org.sopt.app.facade.AuthFacade;
import org.sopt.app.facade.PokeFacade;
import org.sopt.app.facade.RankFacade;
import org.sopt.app.facade.SoptampFacade;
import org.sopt.app.presentation.user.UserResponse.SoptLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/user")
@SecurityRequirement(name = "Authorization")
public class UserController {

    private final SoptampUserService soptampUserService;
    private final SoptampFacade soptampFacade;
    private final AuthFacade authFacade;
    private final PokeFacade pokeFacade;
    private final RankFacade rankFacade;
    private final FortuneService fortuneService;

    @Value("${sopt.current.generation}")
    private Long generation;

    @Value("${cloud.aws.s3.uri}")
    private String s3BaseUrl;

    @Operation(summary = "솝탬프 정보 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping(value = "/soptamp")
    public ResponseEntity<UserResponse.Soptamp> getSoptampInfo(@AuthenticationPrincipal User user) {
        val soptampUser = soptampUserService.getSoptampUserInfo(user.getId());
        val response = UserResponse.Soptamp.builder()
                .nickname(soptampUser.getNickname())
                .profileMessage(soptampUser.getProfileMessage())
                .points(soptampUser.getTotalPoints())
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "한마디 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @PatchMapping("/profile-message")
    public ResponseEntity<UserResponse.ProfileMessage> editProfileMessage(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UserRequest.EditProfileMessageRequest editProfileMessageRequest
    ) {
        val result = soptampFacade.editSoptampUserProfileMessage(user.getId(),
                editProfileMessageRequest.getProfileMessage());
        val response = UserResponse.ProfileMessage.builder()
                .profileMessage(result.getProfileMessage())
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "유저 솝트로그 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping(value = "/sopt-log")
    public ResponseEntity<UserResponse.SoptLog> getUserSoptLog(
            @AuthenticationPrincipal User user, @RequestParam(required = false, value = "ko") boolean partTypeToKorean
    ) {
        int soptLevel = authFacade.getUserSoptLevel(user);
        Long pokeCount = pokeFacade.getUserPokeCount(user.getId());
        PlaygroundProfile playgroundProfile = authFacade.getUserDetails(user);
        Long soptampRank = null;
        Long soptDuring = null;

        Optional<Long> latestGeneration = playgroundProfile.getAllActivities().stream()
                .filter(c -> !c.getPlaygroundPart().getPartName().equals("미상"))
                .map(PlaygroundProfileInfo.ActivityCardinalInfo::getGeneration)
                .max(Comparator.naturalOrder());

        Boolean isActive = latestGeneration.isPresent() && latestGeneration.get().equals(generation);
        
        boolean isFortuneChecked = fortuneService.isExistTodayFortune((user.getId()));
        String fortuneText = isFortuneChecked?fortuneService.getTodayFortuneWordByUserId(user.getId(), LocalDate.now()).title():"오늘 내 운세는?";

        if (Boolean.TRUE.equals(isActive)) {
             soptampRank = rankFacade.findUserRank(user.getId());
        } else {
            List<Long> generations = playgroundProfile.getAllActivities().stream()
                    .map(PlaygroundProfileInfo.ActivityCardinalInfo::getGeneration)
                    .toList();

            if (!generations.isEmpty()) {
                soptDuring = (long) ActivityDurationCalculator.calculate(generations);
            }
        }

        List<String> icons = authFacade.getIcons(Boolean.TRUE.equals(isActive) ? IconType.ACTIVE : IconType.INACTIVE);
        List<String> iconsMutableList = new ArrayList<>(icons);
        List<String> iconPriority = List.of("sop-level", "poke", "soptamp", "duration");
        iconsMutableList.sort(Comparator.comparingInt(s -> {
                    String iconName = s.replaceAll("^" + s3BaseUrl + "sopt-log/|.png$", "");
                    return iconPriority.indexOf(iconName);
                }));

        return ResponseEntity.ok(
                SoptLog.of(soptLevel, pokeCount, soptampRank, soptDuring, isActive, iconsMutableList, playgroundProfile,
                        partTypeToKorean,isFortuneChecked, fortuneText));
    }

}
