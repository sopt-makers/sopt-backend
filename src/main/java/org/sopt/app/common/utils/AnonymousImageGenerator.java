package org.sopt.app.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class AnonymousImageGenerator {

    private static String uri;

    public static String getImageUrl(Boolean isAnonymous) {
        return isAnonymous ? uri + "anonymous.png" : "";
    }

    @Autowired
    private void setBucket(Environment env) {
        uri = env.getProperty("cloud.aws.s3.uri");
    }
}
