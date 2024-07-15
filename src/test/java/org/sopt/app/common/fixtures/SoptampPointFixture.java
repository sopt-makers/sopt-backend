package org.sopt.app.common.fixtures;

import static org.sopt.app.common.fixtures.SoptampUserFixture.SOPTAMP_USER_SERVER_1;
import static org.sopt.app.common.fixtures.SoptampUserFixture.SOPTAMP_USER_PLAN_1;
import static org.sopt.app.common.fixtures.SoptampUserFixture.SOPTAMP_USER_IOS_1;

import java.util.List;
import org.sopt.app.application.soptamp.SoptampPointInfo.PartPoint;
import org.sopt.app.application.soptamp.SoptampPointInfo.PartRank;
import org.sopt.app.application.soptamp.SoptampPointInfo.Point;
import org.sopt.app.domain.entity.SoptampPoint;
import org.sopt.app.domain.enums.Part;

public class SoptampPointFixture {

    public static final Point POINT_1 = Point.builder()
            .id(1L)
            .generation(1L)
            .soptampUserId(SOPTAMP_USER_SERVER_1.getId())
            .points(100L).build();
    public static final Point POINT_2 = Point.builder()
            .id(2L)
            .generation(1L)
            .soptampUserId(SOPTAMP_USER_PLAN_1.getId())
            .points(200L).build();
    public static final Point POINT_3 = Point.builder()
            .id(3L)
            .generation(1L)
            .soptampUserId(SOPTAMP_USER_IOS_1.getId())
            .points(300L).build();
    public static final List<Point> SOPTAMP_POINT_LIST = List.of(POINT_1, POINT_2, POINT_3);

    public static final SoptampPoint SOPTAMP_POINT_1 = SoptampPoint.builder()
            .id(POINT_1.getId())
            .generation(POINT_1.getGeneration())
            .soptampUserId(POINT_1.getSoptampUserId())
            .points(POINT_1.getPoints()).build();
    public static final SoptampPoint SOPTAMP_POINT_2 = SoptampPoint.builder()
            .id(POINT_2.getId())
            .generation(POINT_2.getGeneration())
            .soptampUserId(POINT_2.getSoptampUserId())
            .points(POINT_2.getPoints()).build();
    public static final SoptampPoint SOPTAMP_POINT_3 = SoptampPoint.builder()
            .id(POINT_3.getId())
            .generation(POINT_3.getGeneration())
            .soptampUserId(POINT_3.getSoptampUserId())
            .points(POINT_3.getPoints()).build();

    public static final PartPoint PART_POINT_PLAN = new PartPoint(Part.PLAN, 50L);
    public static final PartPoint PART_POINT_SERVER = new PartPoint(Part.SERVER, 40L);
    public static final PartPoint PART_POINT_WEB = new PartPoint(Part.WEB, 30L);
    public static final PartPoint PART_POINT_IOS = new PartPoint(Part.IOS, 20L);
    public static final PartPoint PART_POINT_ANDROID = new PartPoint(Part.ANDROID, 10L);
    public static final PartPoint PART_POINT_DESIGN = new PartPoint(Part.DESIGN, 0L);
    public static final List<PartPoint> PART_POINTS = List.of(
            PART_POINT_PLAN,
            PART_POINT_DESIGN,
            PART_POINT_WEB,
            PART_POINT_IOS,
            PART_POINT_ANDROID,
            PART_POINT_SERVER
    ); // 기획, 디자인, 웹, 아요, 안드, 서버의 순서가 지켜져야 함.

    public static final PartRank PART_RANK_PLAN = PartRank.builder()
            .part(Part.PLAN.getPartName())
            .rank(1)
            .points(PART_POINT_PLAN.points())
            .build();
    public static final PartRank PART_RANK_SERVER = PartRank.builder()
            .part(Part.SERVER.getPartName())
            .rank(2)
            .points(PART_POINT_SERVER.points())
            .build();
    public static final PartRank PART_RANK_WEB = PartRank.builder()
            .part(Part.WEB.getPartName())
            .rank(3)
            .points(PART_POINT_WEB.points())
            .build();
    public static final PartRank PART_RANK_IOS = PartRank.builder()
            .part(Part.IOS.getPartName())
            .rank(4)
            .points(PART_POINT_IOS.points())
            .build();
    public static final PartRank PART_RANK_ANDROID = PartRank.builder()
            .part(Part.ANDROID.getPartName())
            .rank(5)
            .points(PART_POINT_ANDROID.points())
            .build();
    public static final PartRank PART_RANK_DESIGN = PartRank.builder()
            .part(Part.DESIGN.getPartName())
            .rank(6)
            .points(0L)
            .build();
}

