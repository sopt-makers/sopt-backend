package org.sopt.app.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.app.domain.enums.IconType;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Icons {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String iconUrl;

    @Enumerated(EnumType.STRING)
    private IconType iconType;
}
