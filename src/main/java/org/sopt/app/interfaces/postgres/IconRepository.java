package org.sopt.app.interfaces.postgres;

import java.util.List;
import org.sopt.app.domain.entity.Icons;
import org.sopt.app.domain.enums.IconType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IconRepository extends JpaRepository<Icons,Long> {
    List<Icons> findAllByIconType(IconType iconType);
}
