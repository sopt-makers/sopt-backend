package org.sopt.app.interfaces.postgres;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.sopt.app.domain.entity.AppjamUser;
import org.sopt.app.domain.enums.TeamNumber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppjamUserRepository extends JpaRepository<AppjamUser, Long> {

    List<AppjamUser> findAllByTeamNumber(TeamNumber teamNumber);

    Optional<AppjamUser> findTopByTeamNumberOrderById(TeamNumber teamNumber);

    Optional<AppjamUser> findByUserId(Long userId);

    List<AppjamUser> findAllByTeamNumberIn(Collection<TeamNumber> teamNumbers);
}
