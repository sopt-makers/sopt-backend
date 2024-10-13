package org.sopt.app.presentation.report;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/reports")
@SecurityRequirement(name = "Authorization")
public class ReportController {

    @Value("${report.url}")
    private String formUrl;

    @GetMapping
    public ResponseEntity<String> getReport(){
        return ResponseEntity.ok(formUrl);
    }

}
