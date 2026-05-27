package org.sopt.app.interfaces.postgres.soptletter;

import org.sopt.app.domain.entity.soptletter.SoptLetterLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoptLetterLikeRepository extends JpaRepository<SoptLetterLike, Long> {

}
