package org.sopt.app.interfaces.postgres;

import java.util.List;
import java.util.Optional;
import org.sopt.app.domain.entity.TeamInfo;
import org.sopt.app.domain.enums.TeamNumber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamInfoRepository extends JpaRepository<TeamInfo, Long> {

    List<TeamInfo> findAllByTeamNumber(TeamNumber teamNumber);

    Optional<TeamInfo> findTopByTeamNumberOrderById(TeamNumber teamNumber);
}
