 package org.sopt.app.common.fixtures;

 import java.util.List;
 import org.sopt.app.application.soptamp.SoptampUserInfo;
 import org.sopt.app.domain.entity.soptamp.SoptampUser;
 import org.sopt.app.domain.enums.PlaygroundPart;

 public class SoptampUserFixture {

     private static final Long CURRENT_GENERATION = 37L;

     public static final SoptampUser SOPTAMP_USER_1 =
             SoptampUser.builder().id(1L).userId(10L).nickname("서버유저").totalPoints(100L)
                     .generation(CURRENT_GENERATION).part(PlaygroundPart.SERVER).build();
     public static final SoptampUser SOPTAMP_USER_2 =
             SoptampUser.builder().id(2L).userId(20L).nickname("안드유저").totalPoints(200L)
                     .generation(CURRENT_GENERATION).part(PlaygroundPart.ANDROID).build();
     public static final SoptampUser SOPTAMP_USER_3 =
             SoptampUser.builder().id(3L).userId(30L).nickname("아요유저").totalPoints(300L)
                     .generation(CURRENT_GENERATION).part(PlaygroundPart.IOS).build();
     public static final SoptampUser SOPTAMP_USER_4 =
             SoptampUser.builder().id(4L).userId(40L).nickname("디자인유저").totalPoints(300L)
                     .generation(CURRENT_GENERATION).part(PlaygroundPart.DESIGN).build();
     public static final SoptampUser SOPTAMP_USER_5 =
             SoptampUser.builder().id(5L).userId(50L).nickname("서버유저A").totalPoints(500L)
                     .generation(CURRENT_GENERATION).part(PlaygroundPart.SERVER).build();
     public static final SoptampUser SOPTAMP_USER_6 =
             SoptampUser.builder().id(6L).userId(60L).nickname("서버유저B").totalPoints(600L)
                     .generation(CURRENT_GENERATION).part(PlaygroundPart.SERVER).build();

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
 }
