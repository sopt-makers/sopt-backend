package org.sopt.app.interfaces.postgres;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

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
}
