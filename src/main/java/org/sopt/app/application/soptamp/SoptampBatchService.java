package org.sopt.app.application.soptamp;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Value("${sopt.current.generation}")
    private Long currentGeneration;

    private final SoptampUserService soptampUserService;
    private final AuthUserProfileReader authUserProfileReader;

    /**
     * 전체 솝탬프 유저 upsert 배치 실행
     * - 앱잼 모드: auth DB에서 전체 유저 프로필 조회
     * - 일반 모드: auth DB에서 현재 기수 SOPT 활동 유저 프로필 조회
     * - 청크별 독립 트랜잭션 + 개별 예외 처리 (한 청크 실패가 나머지 청크에 영향 없음)
     * - upsert 멱등이므로 실패 청크는 다음 스케줄 실행 시 재처리됨
     */
    public void upsertAllSoptampUsers() {
        List<PlatformUserInfoResponse> profiles = loadTargetProfiles();
        int total = profiles.size();
        int successCount = 0;
        log.info("솝탬프 유저 upsert 시작 ({}). 총 {}명, 청크 크기: {}", appjamMode ? "앱잼" : "일반", total, BATCH_SIZE);

        for (int i = 0; i < total; i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, total);
            log.info("청크 처리 중: [{}/{}]", end, total);
            successCount += upsertChunk(profiles.subList(i, end), end, total);
        }
        log.info("솝탬프 유저 upsert 완료. 성공: {}명 / 전체: {}명", successCount, total);
    }

    // 앱잼: OB 포함 전체 / 일반: 현재 기수 SOPT 활동 유저
    private List<PlatformUserInfoResponse> loadTargetProfiles() {
        return appjamMode
            ? authUserProfileReader.getAllUserProfiles()
            : authUserProfileReader.getSoptUserProfilesByGeneration(currentGeneration);
    }

    // 청크 단위 처리. 실패해도 다음 청크는 계속 진행하고 성공 인원만 반환
    private int upsertChunk(List<PlatformUserInfoResponse> chunk, int end, int total) {
        try {
            Map<Long, PlatformUserInfoResponse> chunkMap = chunk.stream()
                .collect(Collectors.toMap(p -> (long) p.userId(), p -> p));
            soptampUserService.upsertAllSoptampUsers(chunkMap);
            return chunkMap.size();
        } catch (Exception e) {
            log.error("솝탬프 upsert 청크 처리 실패. chunk=[{}/{}], error={}", end, total, e.getMessage(), e);
            return 0;
        }
    }
}
