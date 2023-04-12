package org.sopt.app.presentation.s3;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class S3Response {

    @Getter
    @Setter
    @ToString
    public static class PreSignedUrl {

        private String preSignedURL;
        private String imageURL;
    }
}
