package org.sopt.app.presentation.stamp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.val;
import org.sopt.app.application.s3.S3Service;
import org.sopt.app.application.stamp.StampService;
import org.sopt.app.domain.entity.User;
import org.sopt.app.presentation.stamp.StampResponse.StampMain;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v2/stamp")
@SecurityRequirement(name = "Authorization")
public class StampDeleteController {

    private final StampService stampService;

    private final S3Service s3Service;


    @Operation(summary = "스탬프 삭제하기(개별)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success", content = @Content),
            @ApiResponse(responseCode = "400", description = "no stamp / no mission", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @DeleteMapping("/{stampId}")
    public ResponseEntity<StampMain> deleteStampById(@AuthenticationPrincipal User user, @PathVariable Long stampId) {
        val fileUrls = stampService.deleteStampById(user, stampId);
        s3Service.deleteFiles(fileUrls, "stamp");
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @Operation(summary = "스탬프 삭제하기(전체)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @DeleteMapping("/all")
    public ResponseEntity<StampResponse.StampMain> deleteStampByUserId(@AuthenticationPrincipal User user) {
        stampService.deleteAllStamps(user);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
