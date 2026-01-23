package org.sopt.app.presentation.stamp;

import java.util.List;
import java.util.Map;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sopt.app.application.soptamp.SoptampUserInfo;
import org.sopt.app.application.stamp.StampInfo;
import org.sopt.app.domain.entity.soptamp.Clap;
import org.springframework.data.domain.Page;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface StampResponseMapper {

    StampResponse.StampMain from(StampInfo.Stamp stampInfo);

    default StampResponse.StampView from(StampInfo.StampView stampView) {
        return StampResponse.StampView.builder()
            .id(stampView.getId())
            .contents(stampView.getContents())
            .images(stampView.getImages())
            .activityDate(stampView.getActivityDate())
            .createdAt(stampView.getCreatedAt())
            .updatedAt(stampView.getUpdatedAt())
            .missionId(stampView.getMissionId())
            .ownerNickname(stampView.getOwnerNickName())
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

    default ClapResponse.ClapUserList of(Page<Clap> page, Map<Long, SoptampUserInfo> profileMap, Map<Long, String> imageMap) {
        List<ClapResponse.ClapUserProfile> users = page.getContent().stream()
                .map(clap -> {
                    var soptampUserInfo = profileMap.get(clap.getUserId());

                    return new ClapResponse.ClapUserProfile(
                            soptampUserInfo.getNickname(),
                            imageMap.getOrDefault(clap.getUserId(), ""),
                            soptampUserInfo.getProfileMessage(),
                            clap.getClapCount()
                    );
                })
                .toList();

        return new ClapResponse.ClapUserList(
                users,
                page.getTotalPages(),
                page.getSize(),
                page.getNumber()
        );
    }
}
