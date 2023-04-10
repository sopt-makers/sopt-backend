package org.sopt.app.v1.presentation;

import static org.sopt.app.common.ResponseCode.SUCCESS;
import static org.sopt.app.common.constants.Constants.RESULT_CODE;

import java.nio.charset.StandardCharsets;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;


@Controller
@AllArgsConstructor
public class BaseController {

    protected HttpHeaders getSuccessHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        headers.set(RESULT_CODE, SUCCESS.getResponseCode());
        return headers;
    }
}
