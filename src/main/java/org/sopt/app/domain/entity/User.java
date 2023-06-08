package org.sopt.app.domain.entity;

import java.util.Collection;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "app_users", schema = "app_dev")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity implements UserDetails {

    @Column
    public String username;
    @Column(nullable = false, unique = true)
    public String nickname;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Column(name = "profile_message")
    private String profileMessage;
    @Column
    private Long points;
    @Column(columnDefinition = "TEXT", name = "push_token")
    @ColumnDefault("")
    private String pushToken;

    @Column(nullable = false, name = "all_opt_in")
    @ColumnDefault("false")
    private Boolean allOptIn;

    @Column(nullable = false, name = "part_opt_in")
    @ColumnDefault("false")
    private Boolean partOptIn;

    @Column(nullable = false, name = "news_opt_in")
    @ColumnDefault("false")
    private Boolean newsOptIn;

    @Column(name = "playground_id", unique = true)
    private Long playgroundId;

    @Column(name = "playground_token")
    private String playgroundToken;

    @Builder
    public User(String username, String nickname, String pushToken, Long playgroundId) {
        this.username = username;
        this.nickname = nickname;
        this.points = 0L;
        this.pushToken = pushToken;
        this.allOptIn = false;
        this.partOptIn = false;
        this.newsOptIn = false;
        this.playgroundId = playgroundId;
    }

    //한 마디 등록
    public void updateProfileMessage(String profileMessage) {
        this.profileMessage = profileMessage;
    }

    //랭크 점수 등록
    public void addPoints(Integer level) {
        this.points = points + level;
    }

    //랭크 점수 마이너스
    public void minusPoints(Integer level) {
        this.points = points - level;
    }

    //랭크 점수 초기화
    public void initializePoints() {
        this.points = 0L;
    }

    public void updatePlaygroundUserInfo(String username, String playgroundToken) {
        this.username = username;
        this.playgroundToken = playgroundToken;
    }

    public void updatePushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public void editNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String getPassword() {
        return null;
    }
}
