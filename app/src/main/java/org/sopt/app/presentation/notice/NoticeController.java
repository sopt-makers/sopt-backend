package org.sopt.app.presentation.notice;

import lombok.AllArgsConstructor;
import org.sopt.app.application.notice.NoticeService;
import org.sopt.app.common.s3.S3Service;
import org.sopt.app.domain.entity.Notice;
import org.sopt.app.presentation.notice.dto.NoticeRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@AllArgsConstructor
public class NoticeController extends BaseController {

    private final NoticeService noticeService;
    private final S3Service s3Service;


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

    /**
     * 공지사항 업로드 하기
     */
    // 게시글 작성
    @PostMapping("/notice")
    public ResponseEntity<?> uploadPost(@RequestPart("noticeContent") NoticeRequestDTO noticeRequestDTO,
                                   @RequestPart("imgUrl") List<MultipartFile> multipartFiles) {
        List<String> imgPaths = s3Service.upload(multipartFiles);
//        System.out.println("IMG 경로들 : " + imgPaths);
        Notice notice = noticeService.uploadPost(noticeRequestDTO, imgPaths);
        return new ResponseEntity<>(notice, getSuccessHeaders(), HttpStatus.OK);
    }
}
