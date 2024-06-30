package org.sopt.app.interfaces.postgres;

import org.sopt.app.application.auth.PlaygroundAuthInfo.RecommendFriendRequest;
import org.sopt.app.domain.entity.RecommendedUsers;
import org.springframework.data.repository.CrudRepository;

public interface RecommendedUserIdsRepository extends CrudRepository<RecommendedUsers, RecommendFriendRequest>{

}
