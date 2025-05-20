package org.sopt.app.presentation.home.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FloatingButtonResponse {
    private String imageUrl;
    private String title;
    private String expandedSubTitle;
    private String collapsedSubtitle;
    private String actionButtonName;
    private String linkUrl;
    private Boolean isActive;

    public static FloatingButtonResponse of(String imageUrl, String title, String expandedSubTitle,
                                            String collapsedSubtitle, String actionButtonName, String linkUrl,
                                            Boolean isActive) {
        return FloatingButtonResponse.builder()
                .imageUrl(imageUrl)
                .title(title)
                .expandedSubTitle(expandedSubTitle)
                .collapsedSubtitle(collapsedSubtitle)
                .actionButtonName(actionButtonName)
                .linkUrl(linkUrl)
                .isActive(isActive)
                .build();
    }
}
