package org.sopt.app.common.utils;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnonymousImageGenerator {

    @Value("${cloud.aws.s3.uri}")
    private static String uri;

    public static String getImageUrl(boolean isAnonymous) {
        return isAnonymous ? uri + "anonymous.png" : "";
    }
}
