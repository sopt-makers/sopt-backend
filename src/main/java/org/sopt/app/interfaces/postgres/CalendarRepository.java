package org.sopt.app.interfaces.postgres;

import java.util.List;
import org.sopt.app.domain.entity.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {

    List<Calendar> findAllByGenerationOrderByStartDateAscEndDateAsc(final Integer generation);
}
