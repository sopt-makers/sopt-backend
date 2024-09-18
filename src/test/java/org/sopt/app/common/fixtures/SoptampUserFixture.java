package org.sopt.app.common.fixtures;

import java.util.List;
import org.sopt.app.application.soptamp.SoptampUserInfo;
import org.sopt.app.domain.entity.soptamp.SoptampUser;

public class SoptampUserFixture {

    public static final SoptampUser SOPTAMP_USER_1 =
            SoptampUser.builder().id(1L).userId(10L).nickname("1stUser").totalPoints(100L).build();
    public static final SoptampUser SOPTAMP_USER_2 =
            SoptampUser.builder().id(2L).userId(20L).nickname("2stUser").totalPoints(200L).build();
    public static final SoptampUser SOPTAMP_USER_3 =
            SoptampUser.builder().id(3L).userId(30L).nickname("3stUser").totalPoints(300L).build();
    public static final List<Long> SOPTAMP_USER_ID_LIST =
            List.of(SOPTAMP_USER_1.getId(), SOPTAMP_USER_2.getId(), SOPTAMP_USER_3.getId());

    public static final SoptampUserInfo SOPTAMP_USER_INFO_1 =
            SoptampUserInfo.builder().id(1L).userId(10L).nickname("1stUser").totalPoints(100L).build();
    public static final SoptampUserInfo SOPTAMP_USER_INFO_2 =
            SoptampUserInfo.builder().id(2L).userId(20L).nickname("2stUser").totalPoints(200L).build();
    public static final SoptampUserInfo SOPTAMP_USER_INFO_3 =
            SoptampUserInfo.builder().id(3L).userId(30L).nickname("3stUser").totalPoints(300L).build();
    public static final List<SoptampUserInfo> SOPTAMP_USER_INFO_LIST =
            List.of(SOPTAMP_USER_INFO_1, SOPTAMP_USER_INFO_2, SOPTAMP_USER_INFO_3);
}
