package org.sopt.app.presentation.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.val;
import org.sopt.app.application.notification.NotificationService;
import org.sopt.app.domain.entity.User;
import org.sopt.app.presentation.notification.NotificationResponse.NotificationMain;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
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
    private final NotificationResponseMapper notificationResponseMapper;

    @Operation(summary = "알림 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping(value = "")
    public ResponseEntity<List<NotificationMain>> findNotificationList(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        val result = notificationService.findNotificationList(user, pageable);
        val response = notificationResponseMapper.ofList(result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @Operation(summary = "알림 단건 등록")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @PostMapping("")
    public ResponseEntity<NotificationMain> registerNotification(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody NotificationRequest.RegisterNotificationRequest registerNotificationRequest
    ) {
        val result = notificationService.registerNotification(user.getId(), registerNotificationRequest);
        val response = notificationResponseMapper.of(result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "알림 읽음 여부 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @PatchMapping(value = "/{notificationId}")
    public ResponseEntity<NotificationResponse.NotificationIsRead> updateNotificationIsRead(
            @AuthenticationPrincipal User user,
            @PathVariable Long notificationId
    ) {
        val result = notificationService.updateNotificationIsRead(user, notificationId);
        val response = notificationResponseMapper.ofIsRead(result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
