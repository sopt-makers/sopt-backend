package org.sopt.app.presentation.stamp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.val;
import org.sopt.app.application.mission.MissionService;
import org.sopt.app.application.s3.S3Service;
import org.sopt.app.application.soptamp.SoptampPointService;
import org.sopt.app.application.soptamp.SoptampUserService;
import org.sopt.app.application.stamp.StampFacade;
import org.sopt.app.application.stamp.StampService;
import org.sopt.app.domain.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    private final MissionService missionService;
    private final SoptampUserService soptampUserService;
    private final SoptampPointService soptampPointService;
    private final S3Service s3Service;
    private final StampFacade stampFacade;

    private final StampResponseMapper stampResponseMapper;

    @Operation(summary = "스탬프 조회하기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "400", description = "no stamp", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("")
    public ResponseEntity<StampResponse.StampMain> findStampByMissionAndUserId(
            @Valid @ModelAttribute StampRequest.FindStampRequest findStampRequest
    ) {
        val userId = soptampUserService.findByNickname(findStampRequest.getNickname()).getUserId();
        val result = stampService.findStamp(findStampRequest, userId);
        val response = stampResponseMapper.of(result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "스탬프 조회하기 - DEPRECATED")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "400", description = "no stamp", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("/mission/{missionId}")
    public ResponseEntity<StampResponse.StampMain> findStampByMissionAndUserIdDeprecated(
            @AuthenticationPrincipal User user,
            @PathVariable Long missionId
    ) {
        val result = stampService.findStampDeprecated(user.getId(), missionId);
        val response = stampResponseMapper.of(result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "스탬프 등록하기 - DEPRECATED")
    @PostMapping("/{missionId}")
    @Transactional
    public ResponseEntity<StampResponse.StampMain> registerStampDeprecated(
            @AuthenticationPrincipal User user,
            @PathVariable Long missionId,
            @RequestPart("stampContent") StampRequest.RegisterStampRequest registerStampRequest,
            @RequestPart(name = "imgUrl", required = false) List<MultipartFile> multipartFiles
    ) {
        val result = stampFacade.uploadStampDeprecated(user.getId(), missionId, registerStampRequest, multipartFiles);
        val response = stampResponseMapper.of(result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "스탬프 수정하기 - DEPRECATED")
    @PutMapping("/{missionId}")
    public ResponseEntity<StampResponse.StampId> editStampDeprecated(
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "400", description = "no mission / duplicate stamp", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @PostMapping("")
    @Transactional
    public ResponseEntity<StampResponse.StampMain> registerStamp(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody StampRequest.RegisterStampRequest registerStampRequest
    ) {
        val result = stampFacade.uploadStamp(user.getId(), registerStampRequest);
        val response = stampResponseMapper.of(result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "스탬프 수정하기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "400", description = "no stamp", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @PutMapping("")
    public ResponseEntity<StampResponse.StampId> editStamp(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody StampRequest.EditStampRequest editStampRequest
    ) {
        val stamp = stampService.editStampContents(editStampRequest, user.getId());
        val response = stampResponseMapper.of(stamp.getId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "스탬프 삭제하기(개별)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success", content = @Content),
            @ApiResponse(responseCode = "400", description = "no stamp / no mission", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @DeleteMapping("/{stampId}")
    public ResponseEntity<StampResponse.StampMain> deleteStampById(
            @AuthenticationPrincipal User user,
            @PathVariable Long stampId
    ) {
        stampFacade.deleteStamp(user.getId(), stampId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @Operation(summary = "스탬프 삭제하기(전체)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @DeleteMapping("/all")
    public ResponseEntity<StampResponse.StampMain> deleteStampByUserId(@AuthenticationPrincipal User user) {
        stampFacade.deleteStampAll(user.getId());
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
