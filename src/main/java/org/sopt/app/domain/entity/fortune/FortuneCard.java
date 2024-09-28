package org.sopt.app.domain.entity.fortune;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
public class FortuneCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String imageUrl;

    private String imageColorCode;

    public static FortuneCard of(String name, String description, String imageUrl, String imageColorCode) {
        return FortuneCard.builder()
                .name(name)
                .description(description)
                .imageUrl(imageUrl)
                .imageColorCode(imageColorCode)
                .build();
    }
}
