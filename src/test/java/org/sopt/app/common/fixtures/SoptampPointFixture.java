package org.sopt.app.common.fixtures;

import java.util.List;
import org.sopt.app.application.soptamp.SoptampPointInfo.PartPoint;
import org.sopt.app.application.soptamp.SoptampPointInfo.PartRank;
import org.sopt.app.domain.enums.Part;

public class SoptampPointFixture {

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

    public static final List<PartPoint> PART_POINTS = List.of(
            new PartPoint(Part.PLAN, 50L),
            new PartPoint(Part.DESIGN, 0L),
            new PartPoint(Part.WEB, 30L),
            new PartPoint(Part.IOS, 20L),
            new PartPoint(Part.ANDROID, 10L),
            new PartPoint(Part.SERVER, 40L));
}

