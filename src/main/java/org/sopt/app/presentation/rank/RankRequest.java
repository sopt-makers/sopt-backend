package org.sopt.app.presentation.rank;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class RankRequest {

    @Getter
    @Setter
    @ToString
    public static class EditProfileMessageRequest {

        private String profileMessage;
    }

}
