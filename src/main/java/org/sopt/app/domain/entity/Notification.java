package org.sopt.app.domain.entity;

import javax.persistence.*;

import javax.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.sopt.app.domain.enums.NotificationCategory;
import org.sopt.app.domain.enums.NotificationType;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private String notificationId;

    @NotNull
    @Column(name = "notification_title", columnDefinition = "TEXT")
    private String title;

    @Column(name = "notification_content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "notification_type")
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @NotNull
    @Column(name = "notification_category")
    @Enumerated(EnumType.STRING)
    private NotificationCategory category;

    private String deepLink;

    private String webLink;

    @NotNull
    @ColumnDefault("false")
    private Boolean isRead;


    public void updateIsRead() {
        this.isRead = true;
    }

}
