package org.sopt.app.domain.entity.soptletter;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.app.domain.entity.BaseEntity;

@Entity
@Table(
    name = "sopt_letter_profile",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_sopt_letter_profile_user",
            columnNames = {"user_id"}
        ),
        @UniqueConstraint(
            name = "uk_sopt_letter_profile_nickname",
            columnNames = {"nickname"}
        )
    }
)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SoptLetterProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String nickname;

    @Builder.Default
    private boolean isOnboarded = false;

    public static SoptLetterProfile of(Long userId, String nickname) {
        return SoptLetterProfile.builder()
                .userId(userId)
                .nickname(nickname)
                .isOnboarded(false)
                .build();
    }

    public void completeOnboarding() {
        this.isOnboarded = true;
    }

}
