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

  @JsonProperty("force_update_version")
  private String forceUpdateVersion;

  @JsonProperty( "app_version")
  private String appVersion;

  private String notice;

  @JsonProperty("img_url")
  private String imgUrl;

}
