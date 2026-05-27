package org.sopt.app.presentation.soptletter;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/sopt-letter")
@SecurityRequirement(name = "Authorization")
public class SoptLetterController {

}
