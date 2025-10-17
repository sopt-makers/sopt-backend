package org.sopt.app.presentation.stamp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.val;
import org.sopt.app.domain.entity.User;
import org.sopt.app.facade.SoptampFacade;
import org.sopt.app.presentation.stamp.StampResponse.SoptampReportResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v2/stamp")
@SecurityRequirement(name = "Authorization")
public class StampController {

    private final SoptampFacade soptampFacade;
    private final StampResponseMapper stampResponseMapper;

    @Operation(summary = "스탬프 조회하기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "400", description = "no stamp", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("")
    public ResponseEntity<StampResponse.StampMain> findStampByMissionAndUserId(
            @Valid @ModelAttribute StampRequest.FindStampRequest findStampRequest
    ) {
        val result = soptampFacade.getStampInfo(findStampRequest.getMissionId(), findStampRequest.getNickname());
        val response = stampResponseMapper.of(result);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "스탬프 등록하기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "400", description = "no mission / duplicate stamp", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @PostMapping("")
    @Transactional
    public ResponseEntity<StampResponse.StampMain> registerStamp(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody StampRequest.RegisterStampRequest registerStampRequest
    ) {
        val result = soptampFacade.uploadStamp(userId, registerStampRequest);
        val response = stampResponseMapper.of(result);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "스탬프 수정하기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "400", description = "no stamp", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @PutMapping("")
    public ResponseEntity<StampResponse.StampId> editStamp(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody StampRequest.EditStampRequest editStampRequest
    ) {
        val stamp = soptampFacade.editStamp(userId, editStampRequest);
        val response = stampResponseMapper.of(stamp.getId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "스탬프 삭제하기(개별)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success", content = @Content),
            @ApiResponse(responseCode = "400", description = "no stamp / no mission", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @DeleteMapping("/{stampId}")
    public ResponseEntity<Void> deleteStampById(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long stampId
    ) {
        soptampFacade.deleteStamp(userId, stampId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "스탬프 삭제하기(전체)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteStampByUserId(@AuthenticationPrincipal Long userId) {
        soptampFacade.deleteStampAll(userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "스탬프에 박수치기")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "success", content = @Content),
        @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @PostMapping("/{stampId}/clap")
    public ResponseEntity<ClapResponse.AddClapResponse> addClap(
        @AuthenticationPrincipal Long userId,
        @PathVariable Long stampId,
        @Valid @RequestBody ClapRequest.AddClapRequest request
    ) {
        int appliedCount = soptampFacade.addClap(userId, stampId, request.getClapCount());
        int totalClapCount = soptampFacade.getStampClapCount(stampId);
        ClapResponse.AddClapResponse response = stampResponseMapper.of(stampId, appliedCount, totalClapCount);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "솝탬프 신고 URL 조회하기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("/report")
    public ResponseEntity<SoptampReportResponse> getReportUrl(){
        return ResponseEntity.ok(soptampFacade.getReportUrl());
    }
}
