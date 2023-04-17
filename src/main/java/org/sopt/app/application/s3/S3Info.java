package org.sopt.app.application.s3;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class S3Info {

    @Getter
    @Builder
    @ToString
    public static class PreSignedUrl {

        private String preSignedURL;
        private String imageURL;
    }
}
