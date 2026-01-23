package org.sopt.app.presentation.appjamtamp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.app.domain.entity.soptamp.Stamp;
import org.sopt.app.presentation.stamp.StampRequest.BaseStampRequest;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppjamtampRequest {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FindStampRequest {

        @Schema(description = "미션 아이디", example = "1")
        @NotNull(message = "missionId may not be null")
        private Long missionId;
        @Schema(description = "닉네임", example = "보핏아무개")
        @NotNull(message = "nickname may not be null")
        private String nickname;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RegisterStampRequest extends BaseStampRequest {

        public RegisterStampRequest(Long missionId, String image, String contents,
            String activityDate) {
            super(missionId, image, contents, activityDate);
        }

        public Stamp toStamp(Long userId) {
            return Stamp.builder()
                .contents(this.getContents())
                .images(List.of(this.getImage()))
                .missionId(this.getMissionId())
                .activityDate(this.getActivityDate())
                .userId(userId)
                .build();
        }
    }

}
