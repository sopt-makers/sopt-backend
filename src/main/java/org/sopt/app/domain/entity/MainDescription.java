package org.sopt.app.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MainDescription{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String activeTopDescription;

    private String activeBottomDescription;

    private String inactiveTopDescription;

    private String inactiveBottomDescription;
}
