package org.sopt.app.application.platform.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PlatformUserInfoWrapper(
        List<PlatformUserInfoResponse> data
) {}