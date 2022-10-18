package org.sopt.app.application.notice;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.sopt.app.domain.entity.Notice;
import org.sopt.app.domain.entity.QNotice;
import org.sopt.app.interfaces.notice.NoticeRepository;
import org.sopt.app.presentation.notice.dto.NoticeRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final EntityManager em;

    public List<Notice> findAllById(Integer notice_id) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QNotice qNotice = QNotice.notice;

        List<Notice> list = queryFactory.select(qNotice)
                .from(qNotice)
                .where(
                        StringUtils.hasText(String.valueOf(notice_id)) ? qNotice.id.eq(Long.valueOf(notice_id)) : null
                ).orderBy(qNotice.id.desc())
                .fetch();
        return list;
    }

    public List<Notice> findNoticeByPartAndTitle(String part, String title) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QNotice qNotice = QNotice.notice;

        List<Notice> list = queryFactory.select(qNotice)
                .from(qNotice)
                .where(
                        StringUtils.hasText(part) ? qNotice.part.eq(part) : null
                        , StringUtils.hasText(title) ? qNotice.title.contains(title) : null
                ).orderBy(qNotice.id.desc())
                .fetch();
        return list;
    }

    // 게시글 작성 -  이미지 포함
    @Transactional
    public Notice uploadPostWithImg(NoticeRequestDTO noticeRequestDTO, List<String> imgPaths) {

        List<String> imgList = new ArrayList<>(imgPaths);
        Notice notice = this.convertNoticeImg(noticeRequestDTO, imgList);
        return noticeRepository.save(notice);

    }

    // 게시글 작성 - 이미지 미포함
    public Notice uploadPost(NoticeRequestDTO noticeRequestDTO) {

        Notice notice = this.convertNotice(noticeRequestDTO);
        return noticeRepository.save(notice);
    }

    //Notice Entity 양식에 맞게 데이터 세팅
    private Notice convertNoticeImg(NoticeRequestDTO noticeRequestDTO, List<String> imgList) {

        Notice notice = Notice.builder()
                .title(noticeRequestDTO.getTitle())
                .contents(noticeRequestDTO.getContents())
                .part(noticeRequestDTO.getPart())
                .images(imgList)
                .creator(noticeRequestDTO.getCreator())
                .scope(noticeRequestDTO.getScope())
                .build();
        return notice;
    }

    private Notice convertNotice(NoticeRequestDTO noticeRequestDTO) {

        Notice notice = Notice.builder()
                .title(noticeRequestDTO.getTitle())
                .contents(noticeRequestDTO.getContents())
                .part(noticeRequestDTO.getPart())
                .creator(noticeRequestDTO.getCreator())
                .scope(noticeRequestDTO.getScope())
                .build();
        return notice;
    }

}
