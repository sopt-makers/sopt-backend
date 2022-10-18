package org.sopt.app.presentation.alert;

import lombok.AllArgsConstructor;
import org.sopt.app.application.alert.AlertService;
import org.sopt.app.presentation.alert.dto.PartRspDTO;
import org.sopt.app.presentation.notice.BaseController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AlertController extends BaseController {

    private final AlertService alertService;
    /**
     * 파트 목록 조회하기
     * @return
     */
    @GetMapping(value = "/alert/part")
    @ResponseBody
    public ResponseEntity<PartRspDTO> findPart() {
        return new ResponseEntity<>(alertService.findPart(), getSuccessHeaders(), HttpStatus.OK);
    }
}
