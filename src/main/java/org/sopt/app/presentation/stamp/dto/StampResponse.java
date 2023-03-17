package org.sopt.app.presentation.stamp.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class StampResponse {

    @Getter
    @Setter
    @ToString
    public static class StampId {

        private Long stampId;
    }
}
