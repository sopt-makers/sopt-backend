package org.sopt.app.domain.entity;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.sopt.app.domain.enums.NotificationCategory;
import org.sopt.app.domain.enums.NotificationType;

@Entity
@Table(name = "notifications", schema = "app_dev")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "notification_id", nullable = false)
    private String notificationId;

    @Column(name = "notification_title", columnDefinition = "TEXT", nullable = false)
    private String title;

    @Column(name = "notification_content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "notification_type")
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(name = "notification_category", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationCategory category;

    @Column(name = "deep_link")
    private String deepLink;

    @Column(name = "web_link")
    private String webLink;

    @Column(nullable = false, name = "is_read")
    @ColumnDefault("false")
    private Boolean isRead;


    public void updateIsRead() {
        this.isRead = true;
    }

}
