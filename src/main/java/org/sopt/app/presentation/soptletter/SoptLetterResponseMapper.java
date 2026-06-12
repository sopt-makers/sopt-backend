package org.sopt.app.presentation.soptletter;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.sopt.app.application.soptletter.SoptLetterInfo;
import org.sopt.app.presentation.soptletter.dto.SoptLetterResponse;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface SoptLetterResponseMapper {

    @Mapping(source = "onboarded", target = "isOnboarded")
    SoptLetterResponse.OnboardingProfileResponse of(SoptLetterInfo.Profile info);

    SoptLetterResponse.ReportFormResponse of(SoptLetterInfo.ReportFormResult info);

    SoptLetterResponse.TopicsResponse of(SoptLetterInfo.TopicListResult result);

    SoptLetterResponse.TopicResponse of(SoptLetterInfo.TopicSummary result);

    SoptLetterResponse.TopicDetailResponse of(SoptLetterInfo.TopicDetail result);

    SoptLetterResponse.WriteMessageResponse of(SoptLetterInfo.MessageResult result);

    SoptLetterResponse.MessageDetailResponse ofDetail(SoptLetterInfo.MessageResult result);
}
