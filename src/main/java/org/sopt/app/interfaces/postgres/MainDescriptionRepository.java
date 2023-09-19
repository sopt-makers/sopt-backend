package org.sopt.app.interfaces.postgres;

import org.sopt.app.domain.entity.MainDescription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MainDescriptionRepository extends JpaRepository<MainDescription, Long> {

}
