package org.sopt.app.interfaces.postgres;

import java.util.List;
import java.util.Optional;

import org.sopt.app.domain.entity.soptamp.Clap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClapRepository extends JpaRepository<Clap, Long> {

	Optional<Clap> findByUserIdAndStampId(Long userId, Long stampId);

	Page<Clap> findAllByStampIdOrderByClapCountDescUpdatedAtDesc(Long stampId, Pageable pageable);

	List<Clap> findAllByUserId(Long userId);
}
