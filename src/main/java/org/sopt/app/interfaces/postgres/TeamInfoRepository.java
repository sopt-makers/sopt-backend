package org.sopt.app.interfaces.postgres;

import org.sopt.app.domain.entity.TeamInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamInfoRepository extends JpaRepository<TeamInfo, Long> {
}
