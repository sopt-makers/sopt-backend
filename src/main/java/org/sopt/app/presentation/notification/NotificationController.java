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
import org.sopt.app.presentation.notification.NotificationResponse.NotificationDetail;
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

    @Operation(summary = "알림 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping(value = "")
    public ResponseEntity<List<NotificationResponse.NotificationSimple>> findNotificationList(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        val result = notificationService.findNotificationList(user, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(
                result.stream()
                        .map((notification) -> NotificationResponse.NotificationSimple.of(
                                notification.getId()
                                , notification.getPlaygroundId()
                                , notification.getTitle()
                                , notification.getContent()
                                , notification.getCategory().name()
                                , notification.getIsRead()
                                , notification.getCreatedAt()
                        )).toList());
    }


    @Operation(summary = "[External] 알림 서버로부터 알림 등록")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @PostMapping("")
    public ResponseEntity registerNotification(
            @Valid @RequestBody NotificationRequest.RegisterNotificationRequest registerNotificationRequest
    ) {
        // TODO : AppUser 가 아닌 외부 Client 로부터 인증 절차 없어도 되는지 논의 (ex. x-api-key, spring security 기능 등)
        notificationService.registerNotification(registerNotificationRequest);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @Operation(summary = "알림 읽음 여부 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @PatchMapping(value = {
            "/{notificationId}", ""
    })
    public ResponseEntity<NotificationDetail> updateNotificationIsRead(
            @AuthenticationPrincipal User user,
            @PathVariable(name = "notificationId", required = false) Long notificationId
    ) {
        notificationService.updateNotificationIsRead(user, notificationId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @Operation(summary = "알림 메인 뷰 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping(value = "/main")
    public ResponseEntity<NotificationResponse.NotificationConfirmStatus> updateNotificationIsRead(
            @AuthenticationPrincipal User user
    ) {
        val result = notificationService.getNotificationConfirmStatus(user);
        return ResponseEntity.status(HttpStatus.OK).body(
                NotificationResponse.NotificationConfirmStatus.of(
                        result
                )
        );
    }

}
