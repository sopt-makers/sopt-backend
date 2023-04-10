package org.sopt.app.domain.entity;

import java.util.Collection;
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
import org.sopt.app.domain.enums.OsType;
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
    @Column
    public String password;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Column
    private String email;
    @Column
    private String clientToken;

    @Column
    private String profileMessage;

    @Column
    private Long points;

    @Column
    @Enumerated(EnumType.STRING)
    private OsType osType;

    @Column(name = "playground_id")
    private Long playgroundId;

    @Column
    private String playgroundToken;

    @Builder
    public User(String email, String username, String nickname, String clientToken, OsType osType, String password,
            Long playgroundId) {
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.osType = osType;
        this.clientToken = clientToken;
        this.playgroundId = playgroundId;
        this.points = 0L;
    }

    public void createProfileMessage(User user, String profileMessage) {
        User.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .password(user.getPassword())
                .clientToken(user.getClientToken())
                .profileMessage(profileMessage)
                .points(user.getPoints())
                .osType(user.getOsType())
                .build();
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
}
