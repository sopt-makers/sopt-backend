package org.sopt.app.application.appjamuser;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.sopt.app.domain.entity.AppjamUser;
import org.sopt.app.domain.enums.TeamNumber;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppjamUserInfo {

    @Getter
    @Builder
    @ToString
    public static class TeamSummary {

        private TeamNumber teamNumber;
        private String teamName;

        public static TeamSummary from(AppjamUser appjamUser) {
            return TeamSummary.builder().
                teamNumber(appjamUser.getTeamNumber())
                .teamName(appjamUser.getTeamName())
                .build();
        }

        public static TeamSummary empty() {
            return TeamSummary.builder()
                .build();
        }
    }
}
