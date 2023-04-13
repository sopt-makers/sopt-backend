package org.sopt.app.v1.presentation.mission;


import java.util.List;
import lombok.AllArgsConstructor;
import org.sopt.app.application.s3.S3Service;
import org.sopt.app.domain.entity.Mission;
import org.sopt.app.v1.application.mission.MissionServiceV1;
import org.sopt.app.v1.presentation.BaseController;
import org.sopt.app.v1.presentation.mission.dto.MissionRequestDto;
import org.sopt.app.v1.presentation.mission.dto.MissionResponseDto;
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
public class MissionControllerV1 extends BaseController {

    private final MissionServiceV1 missionServiceV1;
    private final S3Service s3Service;


    /**
     * 전체 mission 조회하기
     */
    @GetMapping(value = "/all")
    @ResponseBody
    public ResponseEntity<?> findAllMission(@RequestHeader("userId") String userId) {
        return new ResponseEntity<>(missionServiceV1.findAllMission(userId), getSuccessHeaders(),
                HttpStatus.OK);
    }


    /**
     * 미션 업로드 하기
     */
    @PostMapping()
    public ResponseEntity<?> uploadMission(
            @RequestPart("missionContent") MissionRequestDto missionRequestDto,
            @RequestPart(name = "imgUrl", required = false) List<MultipartFile> multipartFiles) {

        //MultipartFile을 리스트에 넣어줬기 때문에 List 내부의 이미지파일에 isEmpty()를 적용해야 한다.
        int checkNum = 1;
        for (MultipartFile image : multipartFiles) {
            if (image.isEmpty()) {
                checkNum = 0;
            }
        }

        MissionResponseDto result = MissionResponseDto.builder().build();
        if (checkNum == 0) {
            Mission mission = missionServiceV1.uploadMission(missionRequestDto);
            result.setMissionId(mission.getId());
        } else {
            List<String> imgPaths = s3Service.uploadDeprecated(multipartFiles);
            Mission uploadMissionWithImg = missionServiceV1.uploadMissionWithImg(missionRequestDto,
                    imgPaths);
            result.setMissionId(uploadMissionWithImg.getId());
        }

        return new ResponseEntity<>(result, getSuccessHeaders(), HttpStatus.OK);
    }


    /**
     * 완료 미션만 조회하기
     */
    @GetMapping("complete")
    public ResponseEntity<?> findCompleteMission(@RequestHeader("userId") String userId) {

        List<Mission> resultMission = missionServiceV1.getCompleteMission(userId);

        return new ResponseEntity<>(resultMission, getSuccessHeaders(), HttpStatus.OK);
    }

    /**
     * 미완료 미션만 조회하기
     */
    @GetMapping("incomplete")
    public ResponseEntity<?> findInCompleteMission(@RequestHeader("userId") String userId) {
        List<Mission> resultMission = missionServiceV1.getIncompleteMission(userId);

        return new ResponseEntity<>(resultMission, getSuccessHeaders(), HttpStatus.OK);
    }

}
