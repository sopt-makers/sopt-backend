package org.sopt.app.interfaces.postgres;

import java.util.Optional;

import org.sopt.app.domain.entity.soptamp.Clap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClapRepository extends JpaRepository<Clap, Long> {

	Optional<Clap> findByUserIdAndStampId(Long userId, Long stampId);
}
