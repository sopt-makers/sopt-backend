package org.sopt.app.interfaces.postgres;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.sopt.app.domain.entity.RecommendedUsers;
import org.springframework.data.repository.CrudRepository;

public interface RecommendedUserIdsRepository extends CrudRepository<RecommendedUsers, String> {

    @NotNull
    Optional<RecommendedUsers> findByCondition(@NotNull String condition);
}
