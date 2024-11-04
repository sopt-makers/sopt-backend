package org.sopt.app.application.s3;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class S3Info {

    @Getter
    @Builder
    @ToString
    public static class PreSignedUrl {

        private String preSignedURL;
        private String imageURL;
    }
}
