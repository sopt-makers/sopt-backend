package org.sopt.app.presentation.mission;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.val;
import org.sopt.app.application.mission.MissionService;
import org.sopt.app.common.s3.S3Service;
import org.sopt.app.presentation.BaseController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/mission")
public class MissionController extends BaseController {

    private final MissionService missionService;
    private final S3Service s3Service;


    //    @Operation(summary = "미션 전체 조회하기")
    @GetMapping(value = "/all")
    @ResponseBody
    public ResponseEntity<?> findAllMission(@RequestHeader("userId") String userId) {
        val result = missionService.findAllMission(userId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    //    @Operation(summary = "미션 생성하기")
    @PostMapping()
    public ResponseEntity<?> registerMission(
            @RequestPart("missionContent") MissionRequest.RegisterMissionRequest registerMissionRequest,
            @RequestPart(name = "imgUrl", required = false) List<MultipartFile> multipartFiles) {

        val mission = missionService.uploadMission(registerMissionRequest);
        val isEmptyFileList = (multipartFiles == null || multipartFiles.get(0).isEmpty());
        if (isEmptyFileList) {
            val response = mission.getId();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            val imgPaths = s3Service.upload(multipartFiles);
            val result = missionService.uploadMissionWithImages(mission, imgPaths);
            val response = result.getId();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

    //    @Operation(summary = "완료 미션만 조회하기")
    @GetMapping("complete")
    public ResponseEntity<?> findCompleteMission(@RequestHeader("userId") String userId) {
        val resultMission = missionService.getCompleteMission(userId);
        return ResponseEntity.status(HttpStatus.OK).body(resultMission);
    }

    //    @Operation(summary = "미완료 미션만 조회하기")
    @GetMapping("incomplete")
    public ResponseEntity<?> findInCompleteMission(@RequestHeader("userId") String userId) {
        val resultMission = missionService.getIncompleteMission(userId);
        return ResponseEntity.status(HttpStatus.OK).body(resultMission);
    }
}
