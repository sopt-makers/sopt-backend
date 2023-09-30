package org.sopt.app.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.sopt.app.domain.enums.NotificationCategory;
import org.sopt.app.domain.enums.NotificationType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
@Table(name = "notifications", schema = "app_dev")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id", nullable = false)
    private Long messageId;

    @Column(name = "playground_id", nullable = false)
    private Long playgroundId;

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

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public void updateIsRead() {
        this.isRead = true;
    }

}
