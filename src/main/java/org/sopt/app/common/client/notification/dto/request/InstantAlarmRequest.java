package org.sopt.app.common.client.notification.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import org.sopt.app.common.exception.ClientException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.enums.NotificationCategory;


@Getter
public class InstantAlarmRequest implements AlarmRequest{
    private Set<String> userIds;
    private String title;
    private String content;
    private String category;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String deepLink;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String webLink;

    public static InstantAlarmRequestBuilder builderWithRequired(
        Set<String> userIds,
        String title,
        String content,
        NotificationCategory category
    ){
        return new InstantAlarmRequestBuilder()
            .userIds(userIds)
            .title(title)
            .content(content)
            .category(category);
    }

    @Builder
    private InstantAlarmRequest(Set<String> userIds, String title, String content,
        NotificationCategory category, String deepLink, String webLink) {
        this.userIds = validateUserIds(userIds);
        this.title = validateTitle(title);
        this.content = validateContent(content);
        this.category = validateCategory(category);
        this.deepLink = deepLink;
        this.webLink = webLink;
    }

    private Set<String> validateUserIds(Set<String> userIds) {
        validateEmpty(userIds);
        return userIds;
    }

    private String validateTitle(String title) {
        validateEmpty(title);
        return title;
    }

    private String validateContent(String content) {
        validateEmpty(content);
        return content;
    }

    private String validateCategory(NotificationCategory category) {
        validateEmpty(category);
        return category.name();
    }

    private <T> void validateEmpty(T content) {
        if(content == null){
            throw new ClientException(ErrorCode.INVALID_PARAMETER);
        }
        if(content instanceof String string && string.isEmpty()){
            throw new ClientException(ErrorCode.INVALID_PARAMETER);
        }
    }
}
