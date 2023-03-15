package org.sopt.app.presentation.stamp;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.val;
import org.sopt.app.application.stamp.StampService;
import org.sopt.app.common.s3.S3Service;
import org.sopt.app.domain.entity.Stamp;
import org.sopt.app.presentation.BaseController;
import org.sopt.app.presentation.stamp.dto.StampRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/stamp")
public class StampController extends BaseController {

    private final StampService stampService;

    private final S3Service s3Service;

    @Operation(summary = "스탬프 조회하기")
    @GetMapping("/{missionId}")
    public ResponseEntity<Stamp> findStampByMissionAndUserId(
            @RequestHeader("userId") String userId, @PathVariable Long missionId) {
        val response = stampService.findStamp(userId, missionId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "스탬프 등록하기")
    @PostMapping("/{missionId}")
    public ResponseEntity<Stamp> uploadStamp(
            @RequestHeader("userId") String userId,
            @PathVariable Long missionId,
            @RequestPart("stampContent") StampRequest.RegisterStampRequest registerStampRequest,
            @RequestPart(name = "imgUrl", required = false) List<MultipartFile> multipartFiles
    ) {
        stampService.checkDuplicateStamp(userId, missionId); // 스탬프 중복 검사체크
        val imgPaths = s3Service.upload(multipartFiles);
        val response = stampService.uploadStamp(registerStampRequest, imgPaths, userId, missionId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

//    @PutMapping("/{missionId}")
//    public ResponseEntity<?> editStamp(
//            @RequestHeader("userId") String userId,
//            @PathVariable Long missionId,
//            @RequestPart(value = "stampContent", required = false) StampRequest stampRequest,
//            @RequestPart(name = "imgUrl", required = false) List<MultipartFile> multipartFiles
//
//    ) {
//        //MultipartFile을 리스트에 넣어줬기 때문에 List 내부의 이미지파일에 isEmpty()를 적용해야 한다.
//        int checkNum = 1;
//        for (MultipartFile image : multipartFiles) {
//            if (image.isEmpty()) {
//                checkNum = 0;
//            }
//        }
//
//        StampResponseDto result = StampResponseDto.builder().build();
//        if (checkNum == 0) {
//
//            Stamp stamp = stampService.editStampContents(stampRequest, userId, missionId);
//            result.setStampId(stamp.getId());
//
//        } else {
//
//            List<String> imgPaths = s3Service.upload(multipartFiles);
//            Stamp uploadStamp = stampService.editStampWithImg(stampRequest,
//                    imgPaths, userId, missionId);
//
//            result.setStampId(uploadStamp.getId());
//        }
//        return new ResponseEntity<>(result, getSuccessHeaders(), HttpStatus.OK);
//    }

    /**
     * Stamp 개별 삭제
     */
    @DeleteMapping("/{stampId}")
    public ResponseEntity<?> deleteStampById(@PathVariable Long stampId) {
        stampService.deleteByStampId(stampId);
        return new ResponseEntity<>("{}", getSuccessHeaders(), HttpStatus.OK);
    }

    /**
     * 전체 Stamp삭제
     */
    @DeleteMapping("/all")
    public ResponseEntity<?> deleteStampByUserId(@RequestHeader Long userId) {
        stampService.deleteStampByUserId(userId);
        return new ResponseEntity<>("{}", getSuccessHeaders(), HttpStatus.OK);
    }
}
