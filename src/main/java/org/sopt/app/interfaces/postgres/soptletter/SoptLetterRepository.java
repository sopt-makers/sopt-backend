package org.sopt.app.interfaces.postgres.soptletter;


import org.sopt.app.domain.entity.soptletter.SoptLetter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoptLetterRepository extends JpaRepository<SoptLetter, Long> {

}
