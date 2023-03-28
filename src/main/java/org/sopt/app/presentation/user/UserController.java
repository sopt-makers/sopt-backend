package org.sopt.app.presentation.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.user.UserService;
import org.sopt.app.domain.entity.User;
import org.sopt.app.presentation.rank.RankRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/user")
@SecurityRequirement(name = "Authorization")
public class UserController {

    private final UserService userService;
    private final UserResponseMapper userResponseMapper;


    @Operation(summary = "유저 정보 조회")
    @GetMapping(value = "")
    public ResponseEntity<UserResponse.Main> getUserInfo(@AuthenticationPrincipal User user) {
        // TODO: 추후 플그쪽 조회해서 제공해야 함
        val response = userResponseMapper.of(user);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "솝탬프 정보 조회")
    @GetMapping(value = "/soptamp")
    public ResponseEntity<UserResponse.Soptamp> getSoptampInfo(@AuthenticationPrincipal User user) {
        val response = userResponseMapper.ofSoptamp(user);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

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

    @Operation(summary = "한마디 편집")
    @PatchMapping("/profileMessage")
    public ResponseEntity<UserResponse.ProfileMessage> editProfileMessage(
            @AuthenticationPrincipal User user,
            @RequestBody RankRequest.EditProfileMessageRequest editProfileMessageRequest
    ) {
        val result = userService.editProfileMessage(user.getId(), editProfileMessageRequest.getProfileMessage());
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
