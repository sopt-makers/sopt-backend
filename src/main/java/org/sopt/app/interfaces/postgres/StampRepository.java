package org.sopt.app.interfaces.postgres;

import java.util.List;
import org.sopt.app.domain.entity.Stamp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StampRepository extends JpaRepository<Stamp, Long> {

  List<Stamp> findAllByUserId(Long userId);

}
