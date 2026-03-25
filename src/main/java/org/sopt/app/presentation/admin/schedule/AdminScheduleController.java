package org.sopt.app.presentation.admin.schedule;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.rank.RankScheduler;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/admin/schedule")
@SecurityRequirement(name = "Authorization")
@Profile("lambda")
public class AdminScheduleController {
    private final RankScheduler rankScheduler;

    @Value("${makers.app.admin.password}")
    private String adminPassword;

    @Operation(summary = "스탬프/포인트 랭킹 데이터 정합성 맞추기")
    @PostMapping("/soptamp/sync-rank-cache")
    public ResponseEntity<Void> syncSoptampRankCache(
        @RequestHeader("x-admin-password") String password
    ){
        validateAdmin(password);
        rankScheduler.executeSoptampRank();
        return ResponseEntity.ok().build();
    }

    private void validateAdmin(String password) {
        if (!adminPassword.equals(password)) {
            throw new BadRequestException(ErrorCode.INVALID_APP_ADMIN_PASSWORD);
        }
    }
}
