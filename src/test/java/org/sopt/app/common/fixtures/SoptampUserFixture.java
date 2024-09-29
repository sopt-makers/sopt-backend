package org.sopt.app.common.fixtures;

import java.util.List;
import org.sopt.app.application.soptamp.SoptampUserInfo;
import org.sopt.app.domain.entity.soptamp.SoptampUser;

public class SoptampUserFixture {

    public static final SoptampUser SOPTAMP_USER_1 =
            SoptampUser.builder().id(1L).userId(10L).nickname("서버1stUser").totalPoints(100L).build();
    public static final SoptampUser SOPTAMP_USER_2 =
            SoptampUser.builder().id(2L).userId(20L).nickname("안드2stUser").totalPoints(200L).build();
    public static final SoptampUser SOPTAMP_USER_3 =
            SoptampUser.builder().id(3L).userId(30L).nickname("아요3stUser").totalPoints(300L).build();
    public static final SoptampUser SOPTAMP_USER_4 =
            SoptampUser.builder().id(4L).userId(40L).nickname("디자인3stUser").totalPoints(300L).build();

    public static final SoptampUserInfo SOPTAMP_USER_INFO_1 = SoptampUserInfo.builder()
            .id(SOPTAMP_USER_1.getId())
            .userId(SOPTAMP_USER_1.getUserId())
            .nickname(SOPTAMP_USER_1.getNickname())
            .totalPoints(SOPTAMP_USER_1.getTotalPoints()).build();
    public static final SoptampUserInfo SOPTAMP_USER_INFO_2 = SoptampUserInfo.builder()
            .id(SOPTAMP_USER_2.getId())
            .userId(SOPTAMP_USER_2.getUserId())
            .nickname(SOPTAMP_USER_2.getNickname())
            .totalPoints(SOPTAMP_USER_2.getTotalPoints()).build();
    public static final SoptampUserInfo SOPTAMP_USER_INFO_3 = SoptampUserInfo.builder()
            .id(SOPTAMP_USER_3.getId())
            .userId(SOPTAMP_USER_3.getUserId())
            .nickname(SOPTAMP_USER_3.getNickname())
            .totalPoints(SOPTAMP_USER_3.getTotalPoints()).build();
    public static final SoptampUserInfo SOPTAMP_USER_INFO_4 = SoptampUserInfo.builder().
            id(SOPTAMP_USER_4.getId())
            .userId(SOPTAMP_USER_4.getUserId())
            .nickname(SOPTAMP_USER_4.getNickname())
            .totalPoints(SOPTAMP_USER_4.getTotalPoints()).build();
    public static final List<SoptampUserInfo> SOPTAMP_USER_INFO_LIST =
            List.of(SOPTAMP_USER_INFO_1, SOPTAMP_USER_INFO_2, SOPTAMP_USER_INFO_3, SOPTAMP_USER_INFO_4);
}
