package org.sopt.app.presentation.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.val;
import org.sopt.app.application.notification.NotificationService;
import org.sopt.app.domain.entity.User;
import org.sopt.app.presentation.notification.NotificationResponse.NotificationDetail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        val result = notificationService.findNotificationList(user, pageable);
        return ResponseEntity.ok(
                result.stream()
                        .map(notification -> NotificationResponse.NotificationSimple.of(
                                notification.getNotificationId()
                                , notification.getUserId()
                                , notification.getTitle()
                                , notification.getContent()
                                , notification.getCategory().name()
                                , notification.getIsRead()
                                , notification.getCreatedAt()
                        )).toList());
    }
    @Operation(summary = "알림 상세 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping(value = "/detail/{notificationId}")
    public ResponseEntity<NotificationResponse.NotificationDetail> findNotificationDetail(
            @AuthenticationPrincipal User user,
            @PathVariable("notificationId") String notificationId
    ) {
        val result = notificationService.findNotification(user, notificationId);
        return ResponseEntity.ok(
                NotificationResponse.NotificationDetail.of(
                        result.getNotificationId(),
                        result.getUserId(),
                        result.getTitle(),
                        result.getContent(),
                        result.getDeepLink(),
                        result.getWebLink(),
                        result.getCreatedAt(),
                        result.getUpdatedAt()
                )
        );
    }

    @Operation(summary = "[External] 알림 서버로부터 알림 등록")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @PostMapping("")
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
            "/read/{notificationId}", "/read"
    })
    public ResponseEntity<Object> updateNotificationIsRead(
            @AuthenticationPrincipal User user,
            @PathVariable(name = "notificationId", required = false) String notificationId
    ) {
        notificationService.updateNotificationIsRead(user, notificationId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "알림 목록 조회 - DEPRECATED")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping(value = "")
    @Deprecated
    public ResponseEntity<List<NotificationResponse.NotificationSimpleDeprecated>> findNotificationListDeprecated(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        val result = notificationService.findNotificationList(user, pageable);
        return ResponseEntity.ok(
                result.stream()
                        .map(notification -> NotificationResponse.NotificationSimpleDeprecated.of(
                                notification.getId()
                                , notification.getUserId()
                                , notification.getTitle()
                                , notification.getContent()
                                , notification.getCategory().name()
                                , notification.getIsRead()
                                , notification.getCreatedAt()
                        )).toList());
    }
    @Operation(summary = "알림 상세 조회 - DEPRECATED")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping(value = "/{notificationId}")
    @Deprecated
    public ResponseEntity<NotificationResponse.NotificationDetailDeprecated> findNotificationDetailDeprecated(
            @AuthenticationPrincipal User user,
            @PathVariable("notificationId") Long notificationId
    ) {
        val result = notificationService.findNotificationDeprecated(user, notificationId);
        return ResponseEntity.ok(
                NotificationResponse.NotificationDetailDeprecated.of(
                        result.getId(),
                        result.getUserId(),
                        result.getTitle(),
                        result.getContent(),
                        result.getDeepLink(),
                        result.getWebLink(),
                        result.getCreatedAt(),
                        result.getUpdatedAt()
                )
        );
    }

    @Operation(summary = "알림 읽음 여부 변경 - DEPRECATED")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @PatchMapping(value = {
            "/{notificationId}", ""
    })
    @Deprecated
    public ResponseEntity<NotificationDetail> updateNotificationIsReadDeprecated(
            @AuthenticationPrincipal User user,
            @PathVariable(name = "notificationId", required = false) Long notificationId
    ) {
        notificationService.updateNotificationIsReadDeprecated(user, notificationId);
        return ResponseEntity.ok().build();
    }
}
