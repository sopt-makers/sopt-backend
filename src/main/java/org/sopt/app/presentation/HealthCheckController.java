package org.sopt.app.presentation;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/v2/health")
@RequiredArgsConstructor
public class HealthCheckController {

    @Operation(summary = "Health Check")
    @GetMapping(value = "")
    @ResponseBody
    public String healthCheck() {
        return "Healthy!!";
    }
}