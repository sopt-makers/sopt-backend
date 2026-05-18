package org.sopt.app.application.soptamp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.app.application.platform.PlatformService;
import org.sopt.app.application.platform.dto.PlatformUserInfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SoptampBatchService {

    private static final int BATCH_SIZE = 100;

    @Value("${makers.app.soptamp.appjam-mode:false}")
    private boolean appjamMode;

    private final SoptampUserService soptampUserService;
    private final PlatformService platformService;
    private final AuthUserProfileReader authUserProfileReader;

    public void upsertAllSoptampUsers() {
        if (appjamMode) {
            upsertFromAuthDb();
        } else {
            upsertFromPlatformApi();
        }
    }

    private void upsertFromAuthDb() {
        Map<Long, PlatformUserInfoResponse> allProfiles = authUserProfileReader.getAllUserProfiles();
        List<Long> userIds = new ArrayList<>(allProfiles.keySet());
        int total = userIds.size();
        log.info("솝탬프 유저 upsert 시작 (앱잼). 총 {}명, 청크 크기: {}", total, BATCH_SIZE);

        int successCount = 0;
        for (int i = 0; i < total; i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, total);
            List<Long> chunkIds = userIds.subList(i, end);
            Map<Long, PlatformUserInfoResponse> chunkMap = chunkIds.stream()
                .collect(Collectors.toMap(id -> id, allProfiles::get));
            log.info("청크 처리 중: [{}/{}]", end, total);
            try {
                soptampUserService.upsertAllSoptampUsers(chunkMap);
                successCount += chunkMap.size();
            } catch (Exception e) {
                log.error("솝탬프 upsert 청크 처리 실패. chunk=[{}/{}], error={}", end, total, e.getMessage(), e);
            }
        }
        log.info("솝탬프 유저 upsert 완료. 성공: {}명 / 전체: {}명", successCount, total);
    }

    private void upsertFromPlatformApi() {
        List<Long> targetUserIds = soptampUserService.getUpsertTargetUserIds();
        int total = targetUserIds.size();
        log.info("솝탬프 유저 upsert 시작 (일반). 총 {}명, 청크 크기: {}", total, BATCH_SIZE);

        int successCount = 0;
        for (int i = 0; i < total; i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, total);
            List<Long> chunk = targetUserIds.subList(i, end);
            log.info("청크 처리 중: [{}/{}]", end, total);
            try {
                Map<Long, PlatformUserInfoResponse> profileMap = platformService.getPlatformUserInfosAsMap(chunk);
                soptampUserService.upsertAllSoptampUsers(profileMap);
                successCount += chunk.size();
            } catch (Exception e) {
                log.error("솝탬프 upsert 청크 처리 실패. chunk=[{}/{}], error={}", end, total, e.getMessage(), e);
            }
        }
        log.info("솝탬프 유저 upsert 완료. 성공: {}명 / 전체: {}명", successCount, total);
    }
}
