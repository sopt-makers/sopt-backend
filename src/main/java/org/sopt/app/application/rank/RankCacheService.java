package org.sopt.app.application.rank;

import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import org.sopt.app.application.soptamp.SoptampUserInfo;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

public interface RankCacheService {
    Set<TypedTuple<Long>> getRanking();

    void createNewRank(Long userId);

    void removeRank(Long userId);

    void incrementScore(Long userId, int score);

    void decreaseScore(Long userId, int score);

    void initScore(Long userId);

    void deleteAll();

    void addAll(List<SoptampUserInfo> userInfos);

    @Nullable
    CachedUserInfo getUserInfo(Long id);

    void updateCachedUserInfo(Long id, CachedUserInfo userInfo);
}
