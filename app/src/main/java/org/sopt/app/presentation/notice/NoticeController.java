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
     * title로 공지 검색 조회
     * scope 로 공지글 범위 설정 (ALL / MEMBER)
     * @param part
     * @param title
     * @param scope
     * @return
     */
    @GetMapping(value = "/notice")
    @ResponseBody
    public ResponseEntity<?> findNoticeByPartAndTitle(@RequestParam(value = "part" , required = false) String part,
                                                      @RequestParam(value = "title" , required = false) String title,
                                                      @RequestParam(value = "scope", required = false) String scope) {
        return new ResponseEntity<>(noticeService.findNoticeByPartAndTitle(part, title, scope), getSuccessHeaders(), HttpStatus.OK);
    }

    /**
     * 공지사항 업로드 하기
     */
    // 게시글 작성
    @PostMapping("/notice")
    public ResponseEntity<?> uploadPost(@RequestPart("noticeContent") NoticeRequestDTO noticeRequestDTO,
                                   @RequestPart("imgUrl") List<MultipartFile> multipartFiles) {

        //MultipartFile을 리스트에 넣어줬기 때문에 List 내부의 이미지파일에 isEmpty()를 적용해야 한다.
        int checkNum = 1;
        for(MultipartFile image: multipartFiles){
            if(image.isEmpty()) checkNum = 0;
        }

        if (checkNum == 0) {
            noticeService.uploadPost(noticeRequestDTO);
        } else {
            List<String> imgPaths = s3Service.upload(multipartFiles);
            noticeService.uploadPostWithImg(noticeRequestDTO, imgPaths);
        }
        return new ResponseEntity<>(getSuccessHeaders(), HttpStatus.OK);
    }

    /**
     * 공지사항 수정 (사진 제외)
     */
    @PutMapping("/notice")
    @ResponseBody
    public ResponseEntity<Notice> modifyNotice(@RequestBody NoticeRequestDTO noticeRequestDTO){
        Notice notice = noticeService.modifyNotice(noticeRequestDTO);
        return new ResponseEntity<>(notice, getSuccessHeaders(), HttpStatus.OK);
    }


    /**
     * 공지사항 삭제
     */
    @DeleteMapping("/notice")
    @ResponseBody
    public ResponseEntity<?> deleteNotice(@RequestBody NoticeRequestDTO noticeRequestDTO){
        noticeService.deleteById(noticeRequestDTO.getId());
        return new ResponseEntity<>("{}", getSuccessHeaders(), HttpStatus.OK);
    }

}
