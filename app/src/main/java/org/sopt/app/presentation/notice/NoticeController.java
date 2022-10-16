package org.sopt.app.presentation.notice;

import lombok.AllArgsConstructor;
import org.sopt.app.application.notice.NoticeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class NoticeController extends BaseController {

    private final NoticeService noticeService;


    /**
     * notice_id로 공지 조회하기
     * @param notice_id
     * @return
     */
    @GetMapping(value = "/notice/{notice_id}")
    @ResponseBody
    public ResponseEntity<?> findNotice(@PathVariable(required = false) Integer notice_id) {
        return new ResponseEntity<>(noticeService.findAllById(notice_id), getSuccessHeaders(), HttpStatus.OK);
    }

    /**
     * part 별 공지조회
     * title로 공지조회
     * @param part
     * @param title
     * @return
     */
    @GetMapping(value = "/notice")
    @ResponseBody
    public ResponseEntity<?> findNoticeByPartandTitle(@RequestParam(value = "part" , required = false) String part,
                                        @RequestParam(value = "title" , required = false) String title) {
        return new ResponseEntity<>(noticeService.findNoticeByPartandTitle(part, title), getSuccessHeaders(), HttpStatus.OK);
    }
}
