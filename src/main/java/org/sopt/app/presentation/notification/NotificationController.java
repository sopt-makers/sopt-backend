package org.sopt.app.presentation.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import jakarta.validation.Valid;
import java.util.Objects;
import lombok.*;
import org.sopt.app.application.notification.NotificationService;
import org.sopt.app.domain.entity.User;
import org.sopt.app.domain.enums.NotificationCategory;
import org.sopt.app.presentation.notification.NotificationResponse.NotificationSimple;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v2/notification")
@SecurityRequirement(name = "Authorization")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "알림 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping(value = "/all")
    public ResponseEntity<List<NotificationResponse.NotificationSimple>> findNotificationList(
            @AuthenticationPrincipal Long userId,
            @RequestParam(name = "category", required = false) NotificationCategory category,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        if(Objects.isNull(category)) {
            val result = notificationService.findNotificationList(userId, pageable);
            return ResponseEntity.ok(result.stream().map(NotificationSimple::of).toList());
        } else {
            val result = notificationService.findNotificationListByCategory(userId, pageable, category);
            return ResponseEntity.ok(result.stream().map(NotificationSimple::of).toList());
        }
    }
    @Operation(summary = "알림 상세 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping(value = "/detail/{notificationId}")
    public ResponseEntity<NotificationResponse.NotificationDetail> findNotificationDetail(
            @AuthenticationPrincipal Long userId,
            @PathVariable("notificationId") String notificationId
    ) {
        val result = notificationService.findNotification(userId, notificationId);
        return ResponseEntity.ok(
                NotificationResponse.NotificationDetail.of(result)
        );
    }

    @Operation(summary = "[External] 알림 서버로부터 알림 등록")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Object> registerNotification(
            @Valid @RequestBody NotificationRequest.RegisterNotificationRequest registerNotificationRequest
    ) {
        notificationService.registerNotification(registerNotificationRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "알림 읽음 여부 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @PatchMapping(value = {
            "/read/{notificationId}", "/read",
            "/{notificationId}", ""
    })
    public ResponseEntity<Object> updateNotificationIsRead(
            @AuthenticationPrincipal Long userId,
            @PathVariable(name = "notificationId", required = false) String notificationId
    ) {
        notificationService.updateNotificationIsRead(userId, notificationId);
        return ResponseEntity.ok().build();
    }
}
