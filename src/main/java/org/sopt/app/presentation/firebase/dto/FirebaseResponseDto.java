package org.sopt.app.presentation.firebase.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FirebaseResponseDto {

  @JsonProperty("iOS_force_update_version")
  private String iosForceUpdateVersion;

  @JsonProperty( "iOS_app_version")
  private String iosAppVersion;

  @JsonProperty("android_force_update_version")
  private String androidForceUpdateVersion;

  @JsonProperty( "android_app_version")
  private String androidAppVersion;

  private String notice;

  @JsonProperty("img_url")
  private String imgUrl;

}
