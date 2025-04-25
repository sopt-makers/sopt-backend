package org.sopt.app.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class AwsConfig {

    @Value("${cloud.aws.region.static}")
    private String regionValue;

    @Bean
    public S3Client s3Client() {
        Region region = Region.of(regionValue);
        return S3Client.builder()
                .region(region)
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        Region region = Region.of(regionValue);
        return S3Presigner.builder()
                .region(region)
                .build();
    }
}
