package org.sopt.app.presentation.alert;

import lombok.AllArgsConstructor;
import org.sopt.app.application.alert.AlertService;
import org.sopt.app.application.alert.command.SaveAlertCommand;
import org.sopt.app.presentation.alert.dto.PartResponseDTO;
import org.sopt.app.presentation.alert.dto.SavePartsRequestDTO;
import org.sopt.app.presentation.alert.dto.FindUserPartResponseDTO;
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
    public ResponseEntity<PartResponseDTO> findPart() {
        return new ResponseEntity<>(alertService.findPart(), getSuccessHeaders(), HttpStatus.OK);
    }

    @GetMapping(value = "/alert/{user_id}")
    public ResponseEntity<FindUserPartResponseDTO> findUserPart(
            @PathVariable(name = "user_id") Long userId
    ) {
        return new ResponseEntity<>(alertService.findPartByUserId(userId), getSuccessHeaders(), HttpStatus.OK);
    }

    @PostMapping(value = "/alert/{user_id}")
    public void saveParts(
            @PathVariable(name = "user_id") Long userId,
            @RequestBody SavePartsRequestDTO partsDto
    ) {
        alertService.saveParts(SaveAlertCommand.builder()
                .userId(userId)
                .partsDto(partsDto)
                .build());
    }
}
