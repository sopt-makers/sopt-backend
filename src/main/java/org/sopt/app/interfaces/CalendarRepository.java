package org.sopt.app.interfaces;

import java.util.List;
import org.sopt.app.domain.entity.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {

    List<Calendar> findAllByGenerationOrderByStartDate(final Integer generation);
}
