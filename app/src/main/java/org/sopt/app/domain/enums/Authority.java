package org.sopt.app.domain.enums;

public enum Authority {
    NON_MEMBER("비회원"),
    ADMIN("임원진"),
    ACTIVE("활동회원"),
    GRADUATED("수료회원");

    Authority(String description) {
    }
}
