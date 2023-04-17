package org.sopt.app.presentation.s3;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.s3.S3Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/s3")
@SecurityRequirement(name = "Authorization")
public class S3Controller {

    private final S3Service s3Service;
    private final S3ResponseMapper s3ResponseMapper;
    private RestTemplate restTemplate = new RestTemplate();

    @Operation(summary = "스탬프 pre-signed url 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "400", description = "url generator error", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping(value = "/stamp")
    public ResponseEntity<S3Response.PreSignedUrl> getStampPreSignedUrl() {
        val result = s3Service.getPreSignedUrl("stamp");
        val response = s3ResponseMapper.of(result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "미션 pre-signed url 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "400", description = "url generator error", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping(value = "/mission")
    public ResponseEntity<S3Response.PreSignedUrl> getMissionPreSignedUrl() {
        val result = s3Service.getPreSignedUrl("mission");
        val response = s3ResponseMapper.of(result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
