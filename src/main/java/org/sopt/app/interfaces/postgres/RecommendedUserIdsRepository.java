package org.sopt.app.interfaces.postgres;

import org.sopt.app.domain.entity.RecommendedUserIds;
import org.springframework.data.repository.CrudRepository;

public interface RecommendedUserIdsRepository extends CrudRepository<RecommendedUserIds, String> {
}
