package org.sopt.app.interfaces.postgres;

import java.util.List;
import java.util.Optional;
import org.sopt.app.domain.entity.SoptampUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoptampUserRepository extends JpaRepository<SoptampUser, Long> {

    Optional<SoptampUser> findByUserId(Long userId);

    Optional<SoptampUser> findUserByNickname(String nickname);

    List<SoptampUser> findAllByNicknameStartingWith(String part);

    List<SoptampUser> findAllByUserIdIn(List<Long> userIdList);
}
