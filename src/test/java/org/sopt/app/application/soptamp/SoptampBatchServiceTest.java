package org.sopt.app.application.soptamp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.stream.LongStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.platform.dto.PlatformUserInfoResponse;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class SoptampBatchServiceTest {

    private static final long CURRENT_GENERATION = 39L;

    @Mock
    SoptampUserService soptampUserService;
    @Mock
    AuthUserProfileReader authUserProfileReader;

    @InjectMocks
    SoptampBatchService soptampBatchService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(soptampBatchService, "appjamMode", false);
        ReflectionTestUtils.setField(soptampBatchService, "currentGeneration", CURRENT_GENERATION);
    }

    @Test
    @DisplayName("일반 모드: 현재 기수 SOPT 활동 유저 프로필을 auth DB에서 받아 100명 단위로 upsert한다")
    void upsertCurrentGenerationProfilesInChunks() {
        when(authUserProfileReader.getSoptUserProfilesByGeneration(CURRENT_GENERATION)).thenReturn(profiles(1, 150));

        soptampBatchService.upsertAllSoptampUsers();

        ArgumentCaptor<Map<Long, PlatformUserInfoResponse>> chunkCaptor = ArgumentCaptor.forClass(Map.class);
        verify(soptampUserService, times(2)).upsertAllSoptampUsers(chunkCaptor.capture());
        List<Map<Long, PlatformUserInfoResponse>> chunks = chunkCaptor.getAllValues();
        assertThat(chunks.get(0)).hasSize(100).containsKey(1L);
        assertThat(chunks.get(1)).hasSize(50).containsKey(150L);
        verify(authUserProfileReader, never()).getAllUserProfiles();
    }

    @Test
    @DisplayName("일반 모드: 한 청크가 실패해도 다음 청크는 처리된다")
    void continuesAfterChunkFailure() {
        when(authUserProfileReader.getSoptUserProfilesByGeneration(CURRENT_GENERATION)).thenReturn(profiles(1, 150));
        doThrow(new RuntimeException("chunk failed")).doNothing()
            .when(soptampUserService).upsertAllSoptampUsers(any());

        soptampBatchService.upsertAllSoptampUsers();

        verify(soptampUserService, times(2)).upsertAllSoptampUsers(any());
    }

    @Test
    @DisplayName("일반 모드: 대상이 없으면 upsert를 호출하지 않는다")
    void skipsWhenNoTarget() {
        when(authUserProfileReader.getSoptUserProfilesByGeneration(CURRENT_GENERATION)).thenReturn(List.of());

        soptampBatchService.upsertAllSoptampUsers();

        verify(soptampUserService, never()).upsertAllSoptampUsers(any());
    }

    @Test
    @DisplayName("앱잼 모드: 전체 유저 프로필을 쓰고 기수별 조회는 하지 않는다")
    void appjamModeUsesAllProfiles() {
        ReflectionTestUtils.setField(soptampBatchService, "appjamMode", true);
        when(authUserProfileReader.getAllUserProfiles()).thenReturn(profiles(1, 1));

        soptampBatchService.upsertAllSoptampUsers();

        verify(authUserProfileReader, never()).getSoptUserProfilesByGeneration(anyLong());
        verify(soptampUserService).upsertAllSoptampUsers(any());
    }

    private static List<PlatformUserInfoResponse> profiles(int from, int to) {
        return LongStream.rangeClosed(from, to).mapToObj(SoptampBatchServiceTest::profile).toList();
    }

    private static PlatformUserInfoResponse profile(Long id) {
        return new PlatformUserInfoResponse(id.intValue(), "name" + id, null, null, null, null,
            (int) CURRENT_GENERATION, List.of());
    }
}
