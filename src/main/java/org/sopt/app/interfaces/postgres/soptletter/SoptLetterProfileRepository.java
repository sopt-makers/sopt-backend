package org.sopt.app.interfaces.postgres.soptletter;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.sopt.app.domain.entity.soptletter.SoptLetterProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SoptLetterProfileRepository extends JpaRepository<SoptLetterProfile, Long> {

    Optional<SoptLetterProfile> findByUserId(Long userId);
    boolean existsByUserId(Long userId);

    @Query("SELECT p.nickname FROM SoptLetterProfile p WHERE p.nickname IN :nicknames")
    Set<String> findExistingNicknames(@Param("nicknames") List<String> nicknames);
}
