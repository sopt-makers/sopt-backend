package org.sopt.app.presentation.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.stamp.StampService;
import org.sopt.app.application.user.UserService;
import org.sopt.app.domain.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/user")
@SecurityRequirement(name = "Authorization")
public class UserWithdrawController {

    private final UserService userService;
    private final StampService stampService;

    @Operation(summary = "탈퇴하기")
    @DeleteMapping(value = "")
    public ResponseEntity<UserResponse.AppUser> withdraw(@AuthenticationPrincipal User user) {
        // TODO: S3 이미지 삭제
        stampService.deleteStampByUserId(user.getId());
        userService.deleteUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}