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

    default StampResponse.StampMain of(StampInfo.Stamp stamp, int userClapCount, boolean isMine){
        return StampResponse.StampMain.builder()
            .id(stamp.getId())
            .contents(stamp.getContents())
            .images(stamp.getImages())
            .activityDate(stamp.getActivityDate())
            .createdAt(stamp.getCreatedAt())
            .updatedAt(stamp.getUpdatedAt())
            .missionId(stamp.getMissionId())
            .clapCount(userClapCount)
            .viewCount(stamp.getViewCount())
            .myClapCount(userClapCount)
            .isMine(isMine)
            .build();
    }

    StampResponse.StampId of(Long stampId);

    default ClapResponse.AddClapResponse of(Long stampId, int appliedCount, int totalClapCount) {
        return new ClapResponse.AddClapResponse(stampId, appliedCount, totalClapCount);
    }
}
