package org.sopt.app.domain.enums;

import java.util.*;
import lombok.*;

@AllArgsConstructor
@Getter
public enum Part {
    PLAN("기획", 1),
    DESIGN("디자인", 2),
    WEB("웹", 3),
    IOS("아요", 4),
    ANDROID("안드", 5),
    SERVER("서버", 6),
    ;

    private final String partName;
    private final int partOrder;

    public static List<Part> getAllParts() {
         return Arrays.stream(Part.class.getEnumConstants())
                 .sorted(Comparator.comparing(Part::getPartOrder))
                 .toList();
    }
}
