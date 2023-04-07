package org.sopt.app.presentation.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.auth.PlaygroundAuthService;
import org.sopt.app.application.user.UserService;
import org.sopt.app.domain.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/user")
@SecurityRequirement(name = "Authorization")
public class UserOriginalController {

    private final UserService userService;
    private final PlaygroundAuthService playgroundAuthService;
    private final UserResponseMapper userResponseMapper;

    @Operation(summary = "메인 뷰 조회")
    @GetMapping(value = "/main")
    public ResponseEntity<?> getMainViewInfo(@AuthenticationPrincipal User user) {
        // TODO: 추후 플그쪽 조회해서 제공해야 함
        val mainViewUser = playgroundAuthService.getPlaygroundUserForMainView(user.getPlaygroundToken());
//        val dummyUser = UserResponse.User.builder().status("ACTIVE").name("김솝트").profileImage("")
//                .generationList(List.of(32L, 30L, 29L)).build();
//        val dummyOperation = UserResponse.Operation.builder().announcement("공지다!").attendanceScore(2D).build();
//        val response = userResponseMapper.ofMainView(dummyUser, dummyOperation);
        return ResponseEntity.status(HttpStatus.OK).body(mainViewUser);
    }

}
