package org.sopt.app.interfaces.postgres;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class StampRepositoryImplTest {

	@Test
	void 오늘_유저랭킹_조회는_미션레벨합과_display_true_조건으로_집계하고_결과를_정상매핑한다() throws Exception {
		// given
		EntityManager em = mock(EntityManager.class);
		Query query = mock(Query.class);

		StampRepositoryImpl repository = new StampRepositoryImpl();
		setField(repository, "em", em);
		setField(repository, "schema", "test_schema");

		ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);

		when(em.createNativeQuery(sqlCaptor.capture())).thenReturn(query);

		// JPA Query는 보통 setParameter가 자기 자신을 반환
		when(query.setParameter(anyString(), any())).thenReturn(query);

		LocalDateTime todayStart = LocalDateTime.of(2026, 1, 11, 0, 0);
		LocalDateTime tomorrowStart = todayStart.plusDays(1);

		List<Object[]> rows = List.of(
			new Object[] { 1L, 7L, Timestamp.valueOf(LocalDateTime.of(2026, 1, 11, 1, 0)) },
			new Object[] { 2L, 3L, Timestamp.valueOf(LocalDateTime.of(2026, 1, 11, 2, 0)) }
		);
		when(query.getResultList()).thenReturn(rows);

		// when
		List<StampRepositoryCustom.AppjamTodayRankSource> result =
			repository.findTodayUserRankSources(todayStart, tomorrowStart);

		// then: SQL 확인(핵심 조건/조인/집계)
		String sql = sqlCaptor.getValue();
		assertThat(sql).contains("FROM test_schema.stamp s");
		assertThat(sql).contains("JOIN test_schema.mission m");
		assertThat(sql).contains("m.mission_id = s.mission_id");
		assertThat(sql).contains("m.display = TRUE");
		assertThat(sql).contains("SUM");
		assertThat(sql).contains("MIN(s.created_at)");

		// then: 파라미터 세팅 확인
		verify(query).setParameter("todayStart", todayStart);
		verify(query).setParameter("tomorrowStart", tomorrowStart);

		// then: 매핑 확인
		assertThat(result).hasSize(2);
		assertThat(result.get(0).userId()).isEqualTo(1L);
		assertThat(result.get(0).todayPoints()).isEqualTo(7L);
		assertThat(result.get(0).firstCertifiedAtToday()).isEqualTo(LocalDateTime.of(2026, 1, 11, 1, 0));
	}

	private static void setField(Object target, String fieldName, Object value) throws Exception {
		Field field = target.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(target, value);
	}
}
