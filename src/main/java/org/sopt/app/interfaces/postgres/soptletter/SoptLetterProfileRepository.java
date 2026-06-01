package org.sopt.app.interfaces.postgres.soptletter;

import java.util.Optional;
import org.sopt.app.domain.entity.soptletter.SoptLetterProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoptLetterProfileRepository extends JpaRepository<SoptLetterProfile, Long> {

    Optional<SoptLetterProfile> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
