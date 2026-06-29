package org.sopt.app.application.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserWithdrawInfo {

    @Getter
    @Builder
    @ToString
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class WithdrawFormResult {

        private String withdrawFormUrl;

        public static WithdrawFormResult from(String withdrawFormUrl) {
            return WithdrawFormResult.builder()
                .withdrawFormUrl(withdrawFormUrl)
                .build();
        }
    }
}
