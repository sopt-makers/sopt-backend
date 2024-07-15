package org.sopt.app.common.fixtures;

import java.util.List;
import org.sopt.app.application.soptamp.SoptampUserInfo;
import org.sopt.app.domain.entity.SoptampUser;
import org.sopt.app.domain.enums.Part;

public class SoptampUserFixture {

    public static final SoptampUser SOPTAMP_USER_SERVER_1 =
            SoptampUser.builder().id(1L).userId(10L).part(Part.SERVER.getPartName())
                    .generation(1L)
                    .nickname("1st server user")
                    .totalPoints(100L).build();
    public static final SoptampUser SOPTAMP_USER_PLAN_1 =
            SoptampUser.builder().id(2L).userId(20L).part(Part.PLAN.getPartName())
                    .generation(1L)
                    .nickname("1st plan user")
                    .totalPoints(200L).build();
    public static final SoptampUser SOPTAMP_USER_IOS_1 =
            SoptampUser.builder().id(3L).userId(30L).part(Part.IOS.getPartName())
                    .generation(1L)
                    .nickname("1st ios user")
                    .totalPoints(300L).build();
    public static final SoptampUser SOPTAMP_USER_SERVER_2 =
            SoptampUser.builder().id(4L).userId(40L).part(Part.SERVER.getPartName())
                    .generation(1L)
                    .nickname("2nd server user")
                    .totalPoints(400L).build();
    public static final SoptampUser SOPTAMP_USER_SERVER_3_NOT_SAME_GENERATION =
            SoptampUser.builder().id(4L).userId(50L).part(Part.SERVER.getPartName())
                    .generation(2L)
                    .nickname("3rd server user")
                    .totalPoints(500L).build();

    public static final List<Long> SOPTAMP_USER_ID_LIST =
            List.of(SOPTAMP_USER_SERVER_1.getId(), SOPTAMP_USER_PLAN_1.getId(), SOPTAMP_USER_IOS_1.getId());

    public static final SoptampUserInfo SOPTAMP_USER_INFO_1 =
            SoptampUserInfo.builder().id(1L).userId(10L).nickname("1stUser").totalPoints(100L).build();
    public static final SoptampUserInfo SOPTAMP_USER_INFO_2 =
            SoptampUserInfo.builder().id(2L).userId(20L).nickname("2stUser").totalPoints(200L).build();
    public static final SoptampUserInfo SOPTAMP_USER_INFO_3 =
            SoptampUserInfo.builder().id(3L).userId(30L).nickname("3stUser").totalPoints(300L).build();
    public static final List<SoptampUserInfo> SOPTAMP_USER_INFO_LIST =
            List.of(SOPTAMP_USER_INFO_1, SOPTAMP_USER_INFO_2, SOPTAMP_USER_INFO_3);
}
