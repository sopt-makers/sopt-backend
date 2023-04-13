package org.sopt.app.presentation.stamp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.val;
import org.sopt.app.application.s3.S3Service;
import org.sopt.app.application.stamp.StampService;
import org.sopt.app.domain.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v2/stamp")
@SecurityRequirement(name = "Authorization")
public class StampController {

    private final StampService stampService;

    private final S3Service s3Service;

    private final StampResponseMapper stampResponseMapper;

    @Operation(summary = "스탬프 조회하기")
    @GetMapping("/mission/{missionId}")
    public ResponseEntity<StampResponse.Main> findStampByMissionAndUserId(
            @AuthenticationPrincipal User user,
            @PathVariable Long missionId
    ) {
        val result = stampService.findStamp(user.getId(), missionId);
        val response = stampResponseMapper.of(result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "스탬프 등록하기 - DEPRECATED")
    @PostMapping("/{missionId}")
    public ResponseEntity<StampResponse.Main> registerStampDeprecated(
            @AuthenticationPrincipal User user,
            @PathVariable Long missionId,
            @RequestPart("stampContent") StampRequest.RegisterStampRequest registerStampRequest,
            @RequestPart(name = "imgUrl", required = false) List<MultipartFile> multipartFiles
    ) {
        stampService.checkDuplicateStamp(user.getId(), missionId);
        val imgPaths = s3Service.uploadDeprecated(multipartFiles);
        val result = stampService.uploadStampDeprecated(registerStampRequest, imgPaths, user.getId(), missionId);
        val response = stampResponseMapper.of(result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "스탬프 수정하기 - DEPRECATED")
    @PutMapping("/{missionId}")
    public ResponseEntity<StampResponse.Id> editStampDeprecated(
            @AuthenticationPrincipal User user,
            @PathVariable Long missionId,
            @RequestPart(value = "stampContent", required = false) StampRequest.EditStampRequest editStampRequest,
            @RequestPart(name = "imgUrl", required = false) List<MultipartFile> multipartFiles
    ) {
        val stamp = stampService.editStampContentsDeprecated(editStampRequest, user.getId(), missionId);
        val imgPaths = s3Service.uploadDeprecated(multipartFiles);
        if (imgPaths.size() > 0) {
            stampService.editStampImagesDeprecated(stamp, imgPaths);
        }
        val response = stampResponseMapper.of(stamp.getId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @Operation(summary = "스탬프 등록하기")
    @PostMapping("/mission/{missionId}")
    public ResponseEntity<StampResponse.Main> registerStamp(
            @AuthenticationPrincipal User user,
            @PathVariable Long missionId,
            @RequestBody StampRequest.RegisterStampRequest registerStampRequest
    ) {
        stampService.checkDuplicateStamp(user.getId(), missionId);
        val result = stampService.uploadStamp(registerStampRequest, user, missionId);
        val response = stampResponseMapper.of(result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "스탬프 수정하기")
    @PutMapping("/mission/{missionId}")
    public ResponseEntity<StampResponse.Id> editStamp(
            @AuthenticationPrincipal User user,
            @PathVariable Long missionId,
            @RequestBody StampRequest.EditStampRequest editStampRequest
    ) {
        val stamp = stampService.editStampContents(editStampRequest, user.getId(), missionId);
        val response = stampResponseMapper.of(stamp.getId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "스탬프 삭제하기(개별)")
    @DeleteMapping("/{stampId}")
    public ResponseEntity<String> deleteStampById(@PathVariable Long stampId) {
        stampService.deleteByStampId(stampId);
        return ResponseEntity.status(HttpStatus.OK).body("{}");
    }

    @Operation(summary = "스탬프 삭제하기(전체)")
    @DeleteMapping("/all")
    public ResponseEntity<String> deleteStampByUserId(@AuthenticationPrincipal User user) {
        stampService.deleteStampByUserId(user.getId());
        return ResponseEntity.status(HttpStatus.OK).body("{}");
    }
}
