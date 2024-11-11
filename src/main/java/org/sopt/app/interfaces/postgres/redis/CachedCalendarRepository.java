package org.sopt.app.interfaces.postgres.redis;

import org.sopt.app.domain.cache.CachedAllCalendarResponse;
import org.springframework.data.repository.CrudRepository;

public interface CachedCalendarRepository extends CrudRepository<CachedAllCalendarResponse, Integer> {

}
