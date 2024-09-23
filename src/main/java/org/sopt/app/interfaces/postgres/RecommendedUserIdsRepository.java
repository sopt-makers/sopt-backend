package org.sopt.app.interfaces.postgres;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.sopt.app.domain.entity.RecommendedUserIds;
import org.springframework.data.repository.CrudRepository;

public interface RecommendedUserIdsRepository extends CrudRepository<RecommendedUserIds, String> {

    @NotNull
    Optional<RecommendedUserIds> findByCondition(@NotNull String condition);
}
