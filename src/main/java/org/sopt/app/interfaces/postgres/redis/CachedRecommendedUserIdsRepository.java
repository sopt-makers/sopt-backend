package org.sopt.app.interfaces.postgres.redis;

import org.sopt.app.domain.cache.CachedRecommendedUserIds;
import org.springframework.data.repository.CrudRepository;

public interface CachedRecommendedUserIdsRepository extends CrudRepository<CachedRecommendedUserIds, String> {
}
