package org.sopt.app.presentation.home;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.app_service.dto.AppServiceEntryStatusResponse;
import org.sopt.app.application.meeting.MeetingResponse;
import org.sopt.app.domain.entity.User;
import org.sopt.app.facade.HomeFacade;
import org.sopt.app.presentation.home.response.EmploymentPostResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/home")
@SecurityRequirement(name = "Authorization")
public class HomeController {

    private final HomeFacade homeFacade;

    @Operation(summary = "홈 메인 문구 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "401", description = "token error", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("/description")
    public ResponseEntity<HomeDescriptionResponse> getHomeMainDescription(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(
                homeFacade.getHomeMainDescription(user)
        );
    }

    @Operation(summary = "앱 서비스 진입 여부 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "401", description = "token error", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("/app-service")
    public ResponseEntity<List<AppServiceEntryStatusResponse>> getAppService(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(
                homeFacade.checkAppServiceEntryStatus(user)
        );
    }

    @Operation(summary = "최근 채용탭 10개 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "401", description = "token error", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("/employments")
    public ResponseEntity<List<EmploymentPostResponse>> getEmploymentPosts(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(
                homeFacade.getHomeEmploymentPost(user)
        );
    }

    @Operation(summary = "전체 모임 확인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "401", description = "token error", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("/meeting/all")
    public ResponseEntity<List<MeetingResponse>> getAllMeeting(
            @AuthenticationPrincipal User user,
            @RequestParam(value = "page") final int page,
            @RequestParam(value = "take") final int take,
            @RequestParam(value = "category") final String category
    ) {
        return ResponseEntity.ok(
                List.of(
                        MeetingResponse.eventActiveDummy(1L),
                        MeetingResponse.studyRecruitingDummy(2L),
                        MeetingResponse.studyPreRecruitingDummy(3L),
                        MeetingResponse.studyClosedDummy(4L),
                        MeetingResponse.studyActiveDummy(5L),
                        MeetingResponse.eventActiveDummy(6L),
                        MeetingResponse.studyRecruitingDummy(7L),
                        MeetingResponse.studyPreRecruitingDummy(8L),
                        MeetingResponse.studyClosedDummy(9L),
                        MeetingResponse.studyActiveDummy(10L)
                )
        );
    }
}
