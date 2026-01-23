package org.sopt.app.interfaces.postgres;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class StampRepositoryImpl implements StampRepositoryCustom {

	@PersistenceContext
	private EntityManager em;

	@Value("${spring.jpa.properties.hibernate.default_schema}")
	private String schema;

	@Override
	public StampCounts incrementClapCountReturning(Long stampId, int increment) {
		// 스키마를 붙여서 안전하게 실행
		final String sql = String.format("""
            UPDATE %s.stamp
               SET clap_count = clap_count + :increment,
                   version     = version + 1
             WHERE id = :id
         RETURNING clap_count, version
        """, schema);

		Query q = em.createNativeQuery(sql);
		q.setParameter("increment", increment);
		q.setParameter("id", stampId);

		Object[] row = (Object[])q.getSingleResult();
		int newClapCount = ((Number)row[0]).intValue();
		long newVersion = ((Number)row[1]).longValue();
		return new StampCounts(newClapCount, newVersion);
	}

	@Override
	public List<AppjamTodayRankSource> findTodayUserRankSources(LocalDateTime todayStart, LocalDateTime tomorrowStart) {
		final String sql = String.format("""
        SELECT
            s.user_id AS user_id,
            COALESCE(SUM(COALESCE(m.level, 0)), 0) AS today_points,
            MIN(s.created_at) AS first_certified_at_today
        FROM %s.stamp s
        JOIN %s.mission m
          ON m.mission_id = s.mission_id
        WHERE s.created_at >= :todayStart
          AND s.created_at <  :tomorrowStart
          AND m.display = TRUE
        GROUP BY s.user_id
        ORDER BY today_points DESC, first_certified_at_today ASC
    """, schema, schema);

		Query query = em.createNativeQuery(sql);
		query.setParameter("todayStart", todayStart);
		query.setParameter("tomorrowStart", tomorrowStart);

		@SuppressWarnings("unchecked")
		List<Object[]> rows = query.getResultList();

		return rows.stream()
			.map(row -> new AppjamTodayRankSource(
				((Number) row[0]).longValue(),
				((Number) row[1]).longValue(),
				toLocalDateTime(row[2])
			))
			.toList();
	}

	private LocalDateTime toLocalDateTime(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof LocalDateTime localDateTime) {
			return localDateTime;
		}
		if (value instanceof Timestamp timestamp) {
			return timestamp.toLocalDateTime();
		}
		if (value instanceof OffsetDateTime offsetDateTime) {
			return offsetDateTime.toLocalDateTime();
		}

		throw new BadRequestException(ErrorCode.INVALID_PARAMETER);
	}
}
