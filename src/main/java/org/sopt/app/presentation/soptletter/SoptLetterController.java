package org.sopt.app.presentation.soptletter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.facade.SoptLetterFacade;
import org.sopt.app.presentation.soptletter.dto.SoptLetterRequest;
import org.sopt.app.presentation.soptletter.dto.SoptLetterResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/sopt-letter")
@SecurityRequirement(name = "Authorization")
public class SoptLetterController {

    private final SoptLetterFacade soptLetterFacade;
    private final SoptLetterResponseMapper soptLetterResponseMapper;

    @Operation(summary = "솝레터 온보딩 프로필 조회 (존재하지 않을 경우 생성)")
    @GetMapping("/onboarding")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "success"),
        @ApiResponse(responseCode = "409", description = "conflict"),
        @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    public ResponseEntity<SoptLetterResponse.OnboardingProfileResponse> getOrCreateOnboardingProfile(
        @AuthenticationPrincipal Long userId
    ) {
        val result = soptLetterFacade.getOrCreateOnboardingProfile(userId);
        return ResponseEntity.ok(soptLetterResponseMapper.of(result));
    }

    @Operation(summary = "솝레터 온보딩 완료 처리")
    @PostMapping("/onboarding/complete")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "success"),
        @ApiResponse(responseCode = "404", description = "not found", content = @Content),
        @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    public ResponseEntity<SoptLetterResponse.OnboardingProfileResponse> completeOnboardingProfile(
        @AuthenticationPrincipal Long userId
    ) {
        val result = soptLetterFacade.completeOnboardingProfile(userId);
        return ResponseEntity.ok(soptLetterResponseMapper.of(result));
    }

    @Operation(summary = "솝레터 주제 목록 조회")
    @GetMapping("/topics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "success"),
        @ApiResponse(responseCode = "403", description = "forbidden", content = @Content),
        @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    public ResponseEntity<SoptLetterResponse.TopicsResponse> getTopics() {
        val result = soptLetterFacade.getTopics();
        return ResponseEntity.ok(soptLetterResponseMapper.of(result));
    }

    @Operation(summary = "솝레터 주제 단일 조회")
    @GetMapping("/topics/{topicId}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "success"),
        @ApiResponse(responseCode = "403", description = "forbidden", content = @Content),
        @ApiResponse(responseCode = "404", description = "not found", content = @Content),
        @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    public ResponseEntity<SoptLetterResponse.TopicDetailResponse> getTopic(
        @PathVariable Long topicId
    ) {
        val result = soptLetterFacade.getTopic(topicId);
        return ResponseEntity.ok(soptLetterResponseMapper.of(result));
    }

    @Operation(summary = "개별 주제 솝레터 메시지 작성")
    @PostMapping("/topics/{topicId}/messages")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "success"),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content),
        @ApiResponse(responseCode = "404", description = "not found", content = @Content),
        @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    public ResponseEntity<SoptLetterResponse.WriteMessageResponse> writeMessage(
        @AuthenticationPrincipal Long userId,
        @PathVariable Long topicId,
        @Valid @RequestBody SoptLetterRequest.WriteMessageRequest request
    ) {
        val result = soptLetterFacade.createSoptLetter(userId, topicId, request.getContent());
        return ResponseEntity.ok(soptLetterResponseMapper.of(result));
    }

    @Operation(summary = "내가 작성한 솝레터 메시지 수정")
    @PatchMapping("/messages/{messageId}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "success"),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content),
        @ApiResponse(responseCode = "403", description = "forbidden", content = @Content),
        @ApiResponse(responseCode = "404", description = "not found", content = @Content),
        @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    public ResponseEntity<SoptLetterResponse.WriteMessageResponse> updateMessage(
        @AuthenticationPrincipal Long userId,
        @PathVariable Long messageId,
        @Valid @RequestBody SoptLetterRequest.UpdateMessageRequest request
    ) {
        val result = soptLetterFacade.updateSoptLetter(userId, messageId, request.getContent());
        return ResponseEntity.ok(soptLetterResponseMapper.of(result));
    }

    @Operation(summary = "내가 작성한 솝레터 메시지 삭제")
    @DeleteMapping("/messages/{messageId}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "success"),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content),
        @ApiResponse(responseCode = "403", description = "forbidden", content = @Content),
        @ApiResponse(responseCode = "404", description = "not found", content = @Content),
        @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    public ResponseEntity<Void> deleteMessage(
        @AuthenticationPrincipal Long userId,
        @PathVariable Long messageId
    ) {
        soptLetterFacade.deleteSoptLetter(userId, messageId);
        return ResponseEntity.ok().build();
    }
}
