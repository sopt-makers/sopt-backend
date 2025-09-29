 package org.sopt.app.common.fixtures;

 import java.util.List;
 import java.util.concurrent.atomic.AtomicInteger;
 import org.sopt.app.application.platform.dto.PlatformUserInfoResponse;
 import org.sopt.app.application.platform.dto.PlatformUserInfoResponse.SoptActivities;
 import org.sopt.app.application.soptamp.SoptampUserInfo;
 import org.sopt.app.domain.entity.soptamp.SoptampUser;
 import org.sopt.app.domain.enums.SoptPart;
 import software.amazon.awssdk.services.s3.endpoints.internal.Value.Int;

 public class SoptampUserFixture {

     public static final Long CURRENT_GENERATION = 37L;

     public static final String USER_NICKNAME = "testNickname";

     public static final String PLATFORM_PART_NAME_SERVER = "서버";
     public static final String PLATFORM_PART_NAME_ANDROID= "안드로이드";
     public static final String PLATFORM_PART_NAME_IOS= "iOS";

     private static final AtomicInteger ID_GENERATOR = new AtomicInteger(1);

     public static final SoptampUser SOPTAMP_USER_1 =
             SoptampUser.builder().id(1L).userId(10L).nickname("서버유저").totalPoints(100L)
                     .generation(CURRENT_GENERATION).part(SoptPart.SERVER).build();
     public static final SoptampUser SOPTAMP_USER_2 =
             SoptampUser.builder().id(2L).userId(20L).nickname("안드유저").totalPoints(200L)
                     .generation(CURRENT_GENERATION).part(SoptPart.ANDROID).build();
     public static final SoptampUser SOPTAMP_USER_3 =
             SoptampUser.builder().id(3L).userId(30L).nickname("아요유저").totalPoints(300L)
                     .generation(CURRENT_GENERATION).part(SoptPart.IOS).build();
     public static final SoptampUser SOPTAMP_USER_4 =
             SoptampUser.builder().id(4L).userId(40L).nickname("디자인유저").totalPoints(300L)
                     .generation(CURRENT_GENERATION).part(SoptPart.DESIGN).build();
     public static final SoptampUser SOPTAMP_USER_5 =
             SoptampUser.builder().id(5L).userId(50L).nickname("서버유저A").totalPoints(500L)
                     .generation(CURRENT_GENERATION).part(SoptPart.SERVER).build();
     public static final SoptampUser SOPTAMP_USER_6 =
             SoptampUser.builder().id(6L).userId(60L).nickname("서버유저B").totalPoints(600L)
                     .generation(CURRENT_GENERATION).part(SoptPart.SERVER).build();

     public static final SoptampUserInfo SOPTAMP_USER_INFO_1 = SoptampUserInfo.of(SOPTAMP_USER_1);
     public static final SoptampUserInfo SOPTAMP_USER_INFO_2 = SoptampUserInfo.of(SOPTAMP_USER_2);
     public static final SoptampUserInfo SOPTAMP_USER_INFO_3 = SoptampUserInfo.of(SOPTAMP_USER_3);
     public static final SoptampUserInfo SOPTAMP_USER_INFO_4 = SoptampUserInfo.of(SOPTAMP_USER_4);
     public static final SoptampUserInfo SOPTAMP_USER_INFO_5 = SoptampUserInfo.of(SOPTAMP_USER_5);
     public static final SoptampUserInfo SOPTAMP_USER_INFO_6 = SoptampUserInfo.of(SOPTAMP_USER_6);

     public static final List<SoptampUserInfo> SOPTAMP_USER_INFO_LIST =
         List.of(SOPTAMP_USER_INFO_1, SOPTAMP_USER_INFO_2, SOPTAMP_USER_INFO_3,
                     SOPTAMP_USER_INFO_4, SOPTAMP_USER_INFO_5, SOPTAMP_USER_INFO_6);

     public static final List<SoptampUserInfo> SERVER_PART_SOPTAMP_USER_INFO_LIST =
         List.of(SOPTAMP_USER_INFO_6, SOPTAMP_USER_INFO_5, SOPTAMP_USER_INFO_1);

     public static final List<SoptampUser> SERVER_PART_SOPTAMP_USER =
         List.of(SOPTAMP_USER_1, SOPTAMP_USER_5, SOPTAMP_USER_6);

     public static SoptampUser getSoptampUser(Long id, Long userId, String nickname) {
        return SoptampUser.builder()
            .id(id)
            .userId(userId)
            .nickname(nickname)
            .totalPoints(100L)
            .generation(CURRENT_GENERATION)
            .part(SoptPart.SERVER)
            .build();
     }

     public static SoptampUser getSoptampUser(Long id, Long userId) {
         return SoptampUser.builder()
             .id(id)
             .userId(userId)
             .nickname(USER_NICKNAME)
             .totalPoints(100L)
             .generation(CURRENT_GENERATION)
             .part(SoptPart.SERVER)
             .build();
     }

     public static SoptampUser getSoptampUserWithTotalPoint(Long id, Long userId, Long totalPoints) {
         return SoptampUser.builder()
             .id(id)
             .userId(userId)
             .nickname(USER_NICKNAME)
             .totalPoints(totalPoints)
             .generation(CURRENT_GENERATION)
             .part(SoptPart.SERVER)
             .build();
     }

     public static SoptActivities getSoptActivities(
         int generation,
         String part
     ) {
         return new SoptActivities(ID_GENERATOR.getAndIncrement(), generation, part, null);
     }

     public static PlatformUserInfoResponse getPlatformUserInfoResponse(
         int userId, List<SoptActivities> soptActivities
     ){
         return new PlatformUserInfoResponse(
             userId,
             "test",
         "testImage",
             "testBirth",
             "010-1234-5678",
             "test@test.com",
             Math.toIntExact(CURRENT_GENERATION),
             soptActivities
         );
     }

 }
