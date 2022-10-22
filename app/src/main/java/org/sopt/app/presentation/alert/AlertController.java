package org.sopt.app.presentation.alert;

import lombok.AllArgsConstructor;
import org.sopt.app.application.alert.AlertService;
import org.sopt.app.application.alert.command.SaveAlertCommand;
import org.sopt.app.presentation.alert.dto.PartsDTO;
import org.sopt.app.presentation.alert.dto.SavePartsDTO;
import org.sopt.app.presentation.notice.BaseController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class AlertController extends BaseController {

    private final AlertService alertService;

    /**
     * 파트 목록 조회하기
     */
    @GetMapping(value = "/alert/part")
    @ResponseBody
    public ResponseEntity<PartsDTO> findPart() {
        return new ResponseEntity<>(alertService.findPart(), getSuccessHeaders(), HttpStatus.OK);
    }

    /**
     * 파트 알림정보 저장
     */

    @PostMapping(value = "/alert/{user_id}")
    public void saveParts(
            @PathVariable(name = "user_id") Long userId,
            @RequestBody SavePartsDTO partsDto
    ) {
        alertService.saveParts(SaveAlertCommand.builder()
                .userId(userId)
                .partsDto(partsDto)
                .build());
    }
}
