package org.sopt.app.presentation.s3;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sopt.app.application.s3.S3Info;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface S3ResponseMapper {

    S3Response.PreSignedUrl of(S3Info.PreSignedUrl url);

}
