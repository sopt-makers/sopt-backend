package org.sopt.app.common.fixtures;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.sopt.app.domain.entity.soptamp.Mission;
import org.sopt.app.presentation.rank.RankResponse;
import org.sopt.app.presentation.rank.RankResponse.RankMission;

public class MissionFixture {

    private static final AtomicLong idGenerator = new AtomicLong(1L);

    public static final Long MISSION_ID = 1L;
    public static final Integer MISSION_LEVEL = 1;
    public static final String MISSION_TITLE = "mission title";
    public static final Boolean MISSION_DISPLAY = true;
    public static final List<String> MISSION_PROFILE_IMAGES = List.of("missionImage1", "missionImage2");


    public static Mission getMission(){
        return Mission.builder()
            .id(idGenerator.getAndIncrement())
            .level(MISSION_LEVEL)
            .title(MISSION_TITLE)
            .profileImage(MISSION_PROFILE_IMAGES)
            .display(MISSION_DISPLAY)
            .build();
    }

    public static Mission getMissionWithTitleAndLevel(String title, Integer level){
        return Mission.builder()
            .id(idGenerator.getAndIncrement())
            .level(level)
            .title(title)
            .profileImage(MISSION_PROFILE_IMAGES)
            .display(MISSION_DISPLAY)
            .build();
    }

    public static RankResponse.RankMission getRankMission(
        Long missionId
    ){
        return new RankMission(
            missionId,
            MISSION_TITLE,
            MISSION_LEVEL,
            MISSION_DISPLAY,
            MISSION_PROFILE_IMAGES
        );
    }
}
