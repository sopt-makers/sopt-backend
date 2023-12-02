package org.sopt.app.interfaces.postgres;

import org.sopt.app.domain.entity.PokeMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PokeMessageRepository extends JpaRepository<PokeMessage, Integer> {

}