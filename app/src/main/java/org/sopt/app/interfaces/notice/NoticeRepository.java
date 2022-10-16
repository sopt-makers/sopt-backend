package org.sopt.app.interfaces.notice;

import org.sopt.app.domain.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Integer> {
}
