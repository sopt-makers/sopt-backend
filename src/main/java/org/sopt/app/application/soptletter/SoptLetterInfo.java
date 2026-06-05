package org.sopt.app.application.soptletter;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.sopt.app.domain.entity.soptletter.SoptLetter;
import org.sopt.app.domain.entity.soptletter.SoptLetterProfile;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SoptLetterInfo {

    @Getter
    @Builder
    @ToString
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Profile {

        private String nickname;
        private boolean isOnboarded;

        public static Profile from(SoptLetterProfile profile) {
            return Profile.builder()
                .nickname(profile.getNickname())
                .isOnboarded(profile.isOnboarded())
                .build();
        }
    }

    @Getter
    @Builder
    @ToString
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MessageResult {
        private Long messageId;
        private Long topicId;
        private String authorNickname;
        private String content;
        private String colorCode;
        private Double rotationDegree;
        private String shapeType;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Integer likeCount;
        private Boolean likedByMe;
        private Boolean mine;

        public static MessageResult of(SoptLetter letter, String nickname, Boolean likedByMe, Boolean mine) {
            return MessageResult.builder()
                .messageId(letter.getId())
                .topicId(letter.getTopicId())
                .authorNickname(nickname)
                .content(letter.getMessage())
                .colorCode(letter.getColor().getHexCode())
                .rotationDegree(letter.getDegree())
                .shapeType(letter.getShapeType().name())
                .createdAt(letter.getCreatedAt())
                .updatedAt(letter.getUpdatedAt())
                .likeCount(letter.getLikeCount())
                .likedByMe(likedByMe)
                .mine(mine)
                .build();
        }
    }
}
