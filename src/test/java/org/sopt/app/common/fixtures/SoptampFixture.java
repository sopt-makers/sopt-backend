 package org.sopt.app.common.fixtures;

 import java.time.LocalDateTime;
 import java.util.List;
 import org.sopt.app.application.soptamp.SoptampUserInfo;
 import org.sopt.app.application.stamp.StampInfo;
 import org.sopt.app.domain.entity.soptamp.Stamp;
 import org.sopt.app.presentation.stamp.StampRequest.EditStampRequest;
 import org.sopt.app.presentation.stamp.StampRequest.RegisterStampRequest;
 import org.springframework.web.multipart.MultipartFile;

 public class SoptampFixture {

     private static final String EDITED = "edited-";

     /* User */
     public static final Long USER_ID = 10L;
     public static final Long SOPTAMP_USER_ID = 100L;
     public static final String NICKNAME = "nickname";

     /* Mission */
     public static final Long MISSION_ID = 1L;
     public static final Integer MISSION_LEVEL = 1;

     /* Stamp */
     public static final Long STAMP_ID = 11L;
     public static final String STAMP_CONTENTS = "stamp contents";
     public static final String STAMP_IMAGE = "stamp image";
     public static final List<String> STAMP_IMG_PATHS = List.of("image");
     public static final String STAMP_ACTIVITY_DATE = "2024.04.08";
     public static final List<MultipartFile> MULTIPART_FILE_LIST = List.of();

     public static final String EDITED_STAMP_CONTENTS = "edited contents";
     public static final String EDITED_STAMP_IMAGE = "edited images";
     public static final String EDITED_STAMP_ACTIVITY_DATE
         = LocalDateTime.of(2025, 9, 23, 0, 0, 0).toString();

     public static Stamp getStamp(Long userId) {
         return Stamp.builder()
             .userId(userId)
             .missionId(MISSION_ID)
             .contents(STAMP_CONTENTS)
             .images(STAMP_IMG_PATHS)
             .activityDate(STAMP_ACTIVITY_DATE)
             .build();
     }

     public static Stamp getStamp(Long userId, Long missionId) {
         return Stamp.builder()
             .userId(userId)
             .missionId(missionId)
             .contents(STAMP_CONTENTS)
             .images(STAMP_IMG_PATHS)
             .activityDate(STAMP_ACTIVITY_DATE)
             .build();
     }

     public static SoptampUserInfo getUserInfo() {
         return SoptampUserInfo.builder().id(SOPTAMP_USER_ID).userId(USER_ID).nickname(NICKNAME).build();
     }

     public static StampInfo.Stamp getStampInfo() {
         return StampInfo.Stamp.builder()
                 .id(STAMP_ID)
                 .contents(STAMP_CONTENTS)
                 .userId(USER_ID)
                 .missionId(MISSION_ID)
                 .images(STAMP_IMG_PATHS)
                 .activityDate(STAMP_ACTIVITY_DATE)
                 .build();
     }

     public static RegisterStampRequest getRegisterStampRequest() {
         return new RegisterStampRequest(MISSION_ID, STAMP_IMAGE, STAMP_CONTENTS, STAMP_ACTIVITY_DATE);
     }

     public static EditStampRequest getEditStampRequest() {
         return new EditStampRequest(MISSION_ID, STAMP_IMAGE, STAMP_CONTENTS, STAMP_ACTIVITY_DATE);
     }

     public static EditStampRequest getEditStampRequestWithMissionId(Long missionId) {
         return new EditStampRequest(missionId, EDITED_STAMP_IMAGE, EDITED_STAMP_CONTENTS, EDITED_STAMP_ACTIVITY_DATE);
     }

 }
