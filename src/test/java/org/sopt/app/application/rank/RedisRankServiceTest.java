package org.sopt.app.application.rank;

import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.common.fixtures.SoptampUserFixture;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class RedisRankServiceTest {

    @Mock
    private RedisTemplate<String, Long> redisTemplate;

    @Mock
    private ZSetOperations<String, Long> zSetOperations;

    @InjectMocks
    private RedisRankService redisRankService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(redisRankService, "currentGeneration", SoptampUserFixture.CURRENT_GENERATION);
    }

    @Test
    @DisplayName("SUCCESS_랭킹 캐시는 현재 기수가 붙은 키에 쓴다")
    void SUCCESS_addAll_writesToCurrentGenerationKey() {
        // given
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);

        // when
        redisRankService.addAll(List.of(SoptampUserFixture.SOPTAMP_USER_INFO_1));

        // then
        verify(zSetOperations).add(eq("soptamp_score:" + SoptampUserFixture.CURRENT_GENERATION), anySet());
    }

    @Test
    @DisplayName("SUCCESS_구버전 기수 설정으로 뜬 프로세스는 자기 기수 키만 지운다")
    void SUCCESS_deleteAll_staleGenerationCannotTouchCurrentKey() {
        // given
        final long staleGeneration = SoptampUserFixture.CURRENT_GENERATION - 1;
        ReflectionTestUtils.setField(redisRankService, "currentGeneration", staleGeneration);

        // when
        redisRankService.deleteAll();

        // then
        verify(redisTemplate).delete("soptamp_score:" + staleGeneration);
    }
}
