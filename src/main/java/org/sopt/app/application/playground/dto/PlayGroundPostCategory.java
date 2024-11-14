package org.sopt.app.application.playground.dto;

public enum PlayGroundPostCategory {
    SOPT_ACTIVITY("SOPT 활동"),
    FREE("자유"),
    PART("파트");
    private final String displayName;

    PlayGroundPostCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

