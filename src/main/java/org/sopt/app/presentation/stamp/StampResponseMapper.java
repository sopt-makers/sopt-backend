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

    StampResponse.StampMain of(StampInfo.Stamp stamp);

    StampResponse.StampId of(Long stampId);

    default ClapResponse.AddClapResponse of(Long stampId, int appliedCount, int totalClapCount) {
        return new ClapResponse.AddClapResponse(stampId, appliedCount, totalClapCount);
    }

    default ClapResponse.ClapUserList of(Page<Clap> page, Map<Long, SoptampUserInfo> profileMap, Map<Long, String> imageMap) {
        List<ClapResponse.ClapUserProfile> users = page.getContent().stream()
                .map(clap -> {
                    var p = profileMap.get(clap.getUserId());

                    return new ClapResponse.ClapUserProfile(
                            p.getNickname(),
                            imageMap.getOrDefault(clap.getUserId(), ""),
                            p.getProfileMessage(),
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
