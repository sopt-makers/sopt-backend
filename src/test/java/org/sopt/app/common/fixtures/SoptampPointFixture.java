package org.sopt.app.common.fixtures;

import static org.sopt.app.common.fixtures.SoptampUserFixture.SOPTAMP_USER_1;
import static org.sopt.app.common.fixtures.SoptampUserFixture.SOPTAMP_USER_2;
import static org.sopt.app.common.fixtures.SoptampUserFixture.SOPTAMP_USER_3;

import java.util.List;
import java.util.Map;
import org.sopt.app.application.soptamp.SoptampPointInfo.PartRank;
import org.sopt.app.application.soptamp.SoptampPointInfo.Point;
import org.sopt.app.domain.enums.Part;

public class SoptampPointFixture {

    public static final Point POINT_1 = Point.builder()
            .id(1L)
            .generation(1L)
            .soptampUserId(SOPTAMP_USER_1.getId())
            .points(100L).build();
    public static final Point POINT_2 = Point.builder()
            .id(2L)
            .generation(1L)
            .soptampUserId(SOPTAMP_USER_2.getId())
            .points(200L).build();
    public static final Point POINT_3 = Point.builder()
            .id(3L)
            .generation(1L)
            .soptampUserId(SOPTAMP_USER_3.getId())
            .points(300L).build();
    public static final List<Point> SOPTAMP_POINT_LIST = List.of(POINT_1, POINT_2, POINT_3);

    public static final PartRank PART_RANK_PLAN = PartRank.builder()
            .part(Part.PLAN.getPartName())
            .rank(1)
            .points(50L)
            .build();
    public static final PartRank PART_RANK_SERVER = PartRank.builder()
            .part(Part.SERVER.getPartName())
            .rank(2)
            .points(40L)
            .build();
    public static final PartRank PART_RANK_WEB = PartRank.builder()
            .part(Part.WEB.getPartName())
            .rank(3)
            .points(30L)
            .build();
    public static final PartRank PART_RANK_IOS = PartRank.builder()
            .part(Part.IOS.getPartName())
            .rank(4)
            .points(20L)
            .build();
    public static final PartRank PART_RANK_ANDROID = PartRank.builder()
            .part(Part.ANDROID.getPartName())
            .rank(5)
            .points(10L)
            .build();
    public static final PartRank PART_RANK_DESIGN = PartRank.builder()
            .part(Part.DESIGN.getPartName())
            .rank(6)
            .points(0L)
            .build();

    public static final Map<Part, Long> PART_AND_TOTAL_POINT_MAP = Map.of(
            Part.PLAN, 50L,
            Part.SERVER, 40L,
            Part.WEB, 30L,
            Part.IOS, 20L,
            Part.ANDROID, 10L,
            Part.DESIGN, 0L
    );
}

