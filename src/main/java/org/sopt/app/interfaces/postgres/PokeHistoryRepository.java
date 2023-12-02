package org.sopt.app.interfaces.postgres;

import org.sopt.app.domain.entity.PokeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PokeHistoryRepository extends JpaRepository<PokeHistory, Integer> {

}