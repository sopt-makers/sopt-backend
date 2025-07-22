package org.sopt.app.presentation.description;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.facade.HomeFacade;
import org.sopt.app.domain.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/description")
@SecurityRequirement(name = "Authorization")
public class DescriptionController {

    private final HomeFacade homeFacade;

    // TODO : 삭제 예정, Deprecated 된 것으로 인지하고 있음
//    @Operation(summary = "메인 문구 조회")
//    @ApiResponses({
//        @ApiResponse(responseCode = "200", description = "success"),
//        @ApiResponse(responseCode = "401", description = "token error", content = @Content),
//        @ApiResponse(responseCode = "500", description = "server error", content = @Content)
//    })
//    @GetMapping("/main")
//    public ResponseEntity<DescriptionResponse.MainDescription> getMainDescription(
//        @AuthenticationPrincipal User user
//    ) {
//        val response = homeFacade.getMainDescriptionForUser(user);
//        return ResponseEntity.ok(
//            DescriptionResponse.MainDescription.builder()
//                .topDescription(response.getTopDescription())
//                .bottomDescription(response.getBottomDescription())
//                .build()
//        );
//    }
}
