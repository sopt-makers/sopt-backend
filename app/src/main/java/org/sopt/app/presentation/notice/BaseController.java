package org.sopt.app.presentation.notice;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;

import java.nio.charset.StandardCharsets;
// import static com.shinsegae.plutus.common.constants.Constants.RESULT_CODE;
// import static com.shinsegae.plutus.common.constants.ResponseCode.SUCCESS;

@Controller
@AllArgsConstructor
public class BaseController {
    protected HttpHeaders getSuccessHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        // headers.set(RESULT_CODE, SUCCESS.getResponseCode());
        return headers;
    }
}
