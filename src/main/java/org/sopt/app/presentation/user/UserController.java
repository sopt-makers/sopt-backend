package org.sopt.app.presentation.user;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.user.UserService;
import org.sopt.app.domain.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/user")
public class UserController {

    private final UserService userService;
    private final UserResponseMapper userResponseMapper;

    @Operation(summary = "닉네임 변경")
    @PatchMapping(value = "/nickname")
    public ResponseEntity<UserResponse.Nickname> editNickname(
            @AuthenticationPrincipal User user,
            @RequestBody UserRequest.EditNicknameRequest editNicknameRequest
    ) {
        val nickname = editNicknameRequest.getNickname();
        val result = userService.editNickname(user, nickname);
        val response = userResponseMapper.of(result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "탈퇴하기")
    @DeleteMapping(value = "/withdraw")
    public ResponseEntity<?> withdraw(@AuthenticationPrincipal User user) {
        userService.deleteUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
