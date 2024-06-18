package org.sopt.app.common.fixtures;

import org.sopt.app.application.soptamp.SoptampUserInfo;
import org.sopt.app.application.stamp.StampInfo;


public class SoptampFixture {

    public static final Long MISSION_ID = 1L;
    public static final Long USER_ID = 10L;
    public static final Long SOPTAMP_USER_ID = 100L;
    public static final String NICKNAME = "nickname";

    public static SoptampUserInfo.SoptampUser getUserInfo() {
        return SoptampUserInfo.SoptampUser.builder().id(SOPTAMP_USER_ID).userId(USER_ID).nickname(NICKNAME).build();
    }

    public static StampInfo.Stamp getStampInfo() {
        return StampInfo.Stamp.builder().id(SOPTAMP_USER_ID).userId(USER_ID).missionId(MISSION_ID).build();
    }
}
