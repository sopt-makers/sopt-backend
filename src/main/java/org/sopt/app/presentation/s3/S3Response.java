package org.sopt.app.presentation.s3;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class S3Response {

    @Getter
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PreSignedUrl {

        @Schema(description = "Pre-Signed URL 주소", example = "https://s3.ap-northeast-2.amazonaws.com/example/6d7cfc58-05a6-4a83-9112-725f3f95d4fd?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20230412T155024Z&X-Amz-SignedHeaders=host&X-Amz-Expires=3599&X-Amz-Credential=AKIAVQPX6FSLOVJS53KL%2F20230412%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Signature=123456123456")
        private String preSignedURL;
        @Schema(description = "업로드 될 Image URL 주소", example = "https://sopt-makers.s3.ap-northeast-2.amazonaws.com/example/6d7cfc58-05a6-4a83-9112-725f3f95d4fd")
        private String imageURL;
    }
}
