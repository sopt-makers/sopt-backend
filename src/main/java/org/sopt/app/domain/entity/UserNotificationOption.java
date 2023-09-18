package org.sopt.app.domain.entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "notification_option", schema = "app_dev")
public class UserNotificationOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "opt_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private User user;


    @Column(nullable = false, name = "all_opt_in")
    @ColumnDefault("false")
    private Boolean allOptIn;

    @Column(nullable = false, name = "part_opt_in")
    @ColumnDefault("false")
    private Boolean partOptIn;

    @Column(nullable = false, name = "news_opt_in")
    @ColumnDefault("false")
    private Boolean newsOptIn;

    @Builder
    public UserNotificationOption(User user) {
        this.user = user;
        // Default : 모든 알림 설정 OFF
        this.allOptIn = false;
        this.partOptIn = false;
        this.newsOptIn = false;
    }

    public void updateAllOptIn(Boolean allOptIn) {
        this.allOptIn = allOptIn;
    }

    public void updatePartOptIn(Boolean partOptIn) {
        this.partOptIn = partOptIn;
    }

    public void updateNewsOptIn(Boolean newsOptIn) {
        this.newsOptIn = newsOptIn;
    }
}
