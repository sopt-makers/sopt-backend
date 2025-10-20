package org.sopt.app.presentation.stamp;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sopt.app.application.stamp.StampInfo;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface StampResponseMapper {

    default StampResponse.StampMain from(StampInfo.StampView stampView) {
        return StampResponse.StampMain.builder()
            .id(stampView.getId())
            .contents(stampView.getContents())
            .images(stampView.getImages())
            .activityDate(stampView.getActivityDate())
            .createdAt(stampView.getCreatedAt())
            .updatedAt(stampView.getUpdatedAt())
            .missionId(stampView.getMissionId())
            .clapCount(stampView.getClapCount())
            .viewCount(stampView.getViewCount())
            .myClapCount(stampView.getMyClapCount())
            .isMine(stampView.isMine())
            .build();
    }

    StampResponse.StampId of(Long stampId);

    default ClapResponse.AddClapResponse of(Long stampId, int appliedCount, int totalClapCount) {
        return new ClapResponse.AddClapResponse(stampId, appliedCount, totalClapCount);
    }
}
