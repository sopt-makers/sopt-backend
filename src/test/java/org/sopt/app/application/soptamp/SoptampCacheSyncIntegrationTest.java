package org.sopt.app.application.soptamp;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.app.application.rank.RankCacheService;
import org.sopt.app.common.event.EventPublisher;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.support.IntegrationTestSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;

@Import({SoptampUserService.class, SoptampEventListener.class, EventPublisher.class})
class SoptampCacheSyncIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private SoptampUserService soptampUserService;

    @MockBean
    private RankCacheService rankCacheService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(soptampUserService, "currentGeneration", 37L);
        ReflectionTestUtils.setField(soptampUserService, "appjamMode", false);
    }

    @Test
    @DisplayName("FAILURE_트랜잭션 롤백 시 AFTER_COMMIT 리스너가 실행되지 않음")
    void FAILURE_rollback_prevent_cache_sync() {
        // given
        final Long nonExistentUserId = -1L;
        final Integer level = 1;

        // when
        try {
            // addPointByLevel 내부에서 findByUserId 실패로 BadRequestException 발생 및 트랜잭션 롤백
            soptampUserService.addPointByLevel(nonExistentUserId, level);
        } catch (BadRequestException e) {
        }

        // then
        // 트랜잭션이 롤백되었으므로 AFTER_COMMIT 리스너가 호출되지 않아야 함
        verify(rankCacheService, never()).updateScore(anyLong(), anyLong());
    }
}