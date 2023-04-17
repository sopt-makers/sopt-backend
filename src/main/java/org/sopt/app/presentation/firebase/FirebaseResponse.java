package org.sopt.app.presentation.firebase;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;


public class FirebaseResponse {

    @Getter
    @Builder
    @ToString
    public static class Main {

        @JsonProperty("iOS_force_update_version")
        private String iosForceUpdateVersion;

        @JsonProperty("iOS_app_version")
        private String iosAppVersion;

        @JsonProperty("android_force_update_version")
        private String androidForceUpdateVersion;

        @JsonProperty("android_app_version")
        private String androidAppVersion;

        private String notice;

        @JsonProperty("img_url")
        private String imgUrl;
    }
}
