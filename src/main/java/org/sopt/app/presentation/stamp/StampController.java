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
import org.sopt.app.presentation.stamp.dto.StampResponse;
import org.sopt.app.presentation.stamp.dto.StampResponse.StampId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @Operation(summary = "스탬프 수정하기")
    @PutMapping("/{missionId}")
    public ResponseEntity<StampResponse.StampId> editStamp(
            @RequestHeader("userId") String userId,
            @PathVariable Long missionId,
            @RequestPart(value = "stampContent", required = false) StampRequest.EditStampRequest editStampRequest,
            @RequestPart(name = "imgUrl", required = false) List<MultipartFile> multipartFiles

    ) {
        StampResponse.StampId response = new StampId();
        if (multipartFiles == null || multipartFiles.get(0).isEmpty()) {
            val result = stampService.editStampContents(editStampRequest, userId, missionId);
            response.setStampId(result.getId());
        } else {
            List<String> imgPaths = s3Service.upload(multipartFiles);
            val result = stampService.editStampWithImg(editStampRequest,
                    imgPaths, userId, missionId);
            response.setStampId(result.getId());
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

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
