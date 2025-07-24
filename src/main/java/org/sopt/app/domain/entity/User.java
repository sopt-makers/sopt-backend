package org.sopt.app.domain.entity;

import lombok.*;
import java.util.Collection;
import java.util.Collections;
import jakarta.persistence.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "app_users")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    // private String username;

    // @Column(unique = true)
    // private Long playgroundId;

    // private String playgroundToken;

    // @Builder
    // public User(String username, Long playgroundId) {
    //     this.username = username;
    //     this.playgroundId = playgroundId;
    // }

    // public void updatePlaygroundUserInfo(String username, String playgroundToken) {
    //     this.username = username;
    //     this.playgroundToken = playgroundToken;
    // }

    // public void updatePlaygroundToken(String playgroundToken) {
    //     this.playgroundToken = playgroundToken;
    // }

    // UserDetails Override Methods
    // @Override
    // public Collection<? extends GrantedAuthority> getAuthorities() {
    //     return Collections.emptyList();
    // }
    //
    // @Override
    // public boolean isAccountNonExpired() {
    //     return false;
    // }
    //
    // @Override
    // public boolean isAccountNonLocked() {
    //     return false;
    // }
    //
    // @Override
    // public boolean isCredentialsNonExpired() {
    //     return false;
    // }
    //
    // @Override
    // public boolean isEnabled() {
    //     return false;
    // }
    //
    // @Override
    // public String getPassword() {
    //     return null;
    // }
}
