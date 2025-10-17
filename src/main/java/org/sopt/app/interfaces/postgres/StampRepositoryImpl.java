package org.sopt.app.interfaces.postgres;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Repository;

@Repository
public class StampRepositoryImpl implements StampRepositoryCustom {

	@PersistenceContext
	private EntityManager em;

	@Override
	public StampCounts incrementClapCountReturning(Long stampId, int increment) {
		// Postgres 네이티브. 버전 증가까지 함께 처리해 JPA @Version과 의미 일치.
		String sql = """
			    UPDATE stamp
			       SET clap_count = clap_count + :increment,
			           version     = version + 1
			     WHERE id = :id
			 RETURNING clap_count, version
			""";

		Query q = em.createNativeQuery(sql);
		q.setParameter("increment", increment);
		q.setParameter("id", stampId);

		Object[] row = (Object[])q.getSingleResult();
		int newClapCount = ((Number)row[0]).intValue();
		long newVersion = ((Number)row[1]).longValue();
		return new StampCounts(newClapCount, newVersion);
	}
}
