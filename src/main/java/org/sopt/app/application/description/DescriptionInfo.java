package org.sopt.app.application.description;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class DescriptionInfo {

    public interface MainDescription {
        public String getTopDescription();
        public String getBottomDescription();
    }

    @Getter
    @Builder
    @ToString
    public static class ActiveMainDescription implements MainDescription {
        private String activeTopDescription;
        private String activeBottomDescription;

        @Override
        public String getTopDescription() {
            return activeTopDescription;
        }

        @Override
        public String getBottomDescription() {
            return activeBottomDescription;
        }
    }

    @Getter
    @Builder
    @ToString
    public static class InactiveMainDescription implements MainDescription{
        private String inactiveTopDescription;
        private String inactiveBottomDescription;

        @Override
        public String getTopDescription() {
            return inactiveTopDescription;
        }

        @Override
        public String getBottomDescription() {
            return inactiveBottomDescription;
        }
    }
}
