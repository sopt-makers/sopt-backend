package org.sopt.app.interfaces.postgres;

import org.sopt.app.domain.entity.PokeMessage;
import org.sopt.app.domain.enums.PokeMessageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PokeMessageRepository extends JpaRepository<PokeMessage, Long> {

    List<PokeMessage> findAllByType(PokeMessageType type);
}