package org.sopt.app.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.soptletter.SoptLetterInfo;
import org.sopt.app.application.soptletter.SoptLetterInfo.Profile;
import org.sopt.app.application.soptletter.SoptLetterService;

@ExtendWith(MockitoExtension.class)
class SoptLetterFacadeTest {

    @Mock
    private SoptLetterService soptLetterService;

    @InjectMocks
    private SoptLetterFacade soptLetterFacade;

    @Test
    @DisplayName("SUCCESS_온보딩 프로필 정보를 정상적으로 조회하거나 생성한다")
    void SUCCESS_getOrCreateOnboardingProfile() {
        // given
        final Long userId = 1L;
        final String generatedNickname = "익명의 솝트";
        Profile expected = Profile.builder()
                .nickname(generatedNickname)
                .isOnboarded(false)
                .build();
        when(soptLetterService.getOrCreateProfile(userId)).thenReturn(expected);

        // when
        Profile result = soptLetterFacade.getOrCreateOnboardingProfile(userId);

        // then
        assertThat(result.getNickname()).isEqualTo(generatedNickname);
        assertThat(result.isOnboarded()).isFalse();
        verify(soptLetterService, times(1)).getOrCreateProfile(userId);
    }

    @Test
    @DisplayName("SUCCESS_온보딩 완료 처리 파사드가 서비스 메서드를 정상 호출하고 반환한다")
    void SUCCESS_completeOnboardingProfile() {
        // given
        final Long userId = 1L;
        final String nickname = "익명의 솝트";
        Profile expected = Profile.builder()
                .nickname(nickname)
                .isOnboarded(true)
                .build();
        when(soptLetterService.completeOnboarding(userId)).thenReturn(expected);

        // when
        Profile result = soptLetterFacade.completeOnboardingProfile(userId);

        // then
        assertThat(result.getNickname()).isEqualTo(nickname);
        assertThat(result.isOnboarded()).isTrue();
        verify(soptLetterService, times(1)).completeOnboarding(userId);
    }

    @Test
    @DisplayName("SUCCESS_주제별 메시지 목록 조회 파사드가 서비스 메서드를 정상 호출하고 반환한다")
    void SUCCESS_getTopicMessages() {
        // given
        final Long userId = 1L;
        final Long topicId = 3L;
        final Long cursor = 120L;
        final Integer size = 20;

        SoptLetterInfo.TopicMessageSummary message = SoptLetterInfo.TopicMessageSummary.builder()
                .messageId(119L)
                .authorNickname("반짝이는 고래")
                .previewContent("커서 이후 메시지")
                .colorCode("#CCFFEC")
                .rotationDegree(4.0)
                .shapeType("POINT")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .likeCount(0)
                .likedByMe(false)
                .mine(false)
                .build();
        SoptLetterInfo.TopicMessageListResult expected = SoptLetterInfo.TopicMessageListResult.builder()
                .topicId(topicId)
                .title("36기 회고")
                .totalCount(1)
                .nextCursor(119L)
                .hasNext(false)
                .messages(List.of(message))
                .build();

        when(soptLetterService.getTopicMessages(eq(userId), eq(topicId), eq(cursor), eq(size))).thenReturn(expected);

        // when
        SoptLetterInfo.TopicMessageListResult result = soptLetterFacade.getTopicMessages(userId, topicId, cursor, size);

        // then
        assertThat(result.getTopicId()).isEqualTo(topicId);
        assertThat(result.getMessages()).hasSize(1);
        assertThat(result.getNextCursor()).isEqualTo(119L);
        verify(soptLetterService, times(1)).getTopicMessages(eq(userId), eq(topicId), eq(cursor), eq(size));
    }

    @Test
    @DisplayName("SUCCESS_익명 신고 폼 주소 조회 파사드가 서비스 메서드를 정상 호출하고 반환한다")
    void SUCCESS_getReportForm() {
        // given
        final String reportFormUrl = "https://example.com/sopt-letter-report";
        SoptLetterInfo.ReportFormResult expected = SoptLetterInfo.ReportFormResult.builder()
                .reportFormUrl(reportFormUrl)
                .build();
        when(soptLetterService.getReportForm()).thenReturn(expected);

        // when
        SoptLetterInfo.ReportFormResult result = soptLetterFacade.getReportForm();

        // then
        assertThat(result.getReportFormUrl()).isEqualTo(reportFormUrl);
        verify(soptLetterService, times(1)).getReportForm();
    }

    @Test
    @DisplayName("SUCCESS_솝레터 주제 목록 조회 파사드가 서비스 메서드를 정상 호출하고 반환한다")
    void SUCCESS_getTopics() {
        // given
        SoptLetterInfo.TopicSummary topic = SoptLetterInfo.TopicSummary.builder()
                .topicId(3L)
                .title("36기 회고")
                .createdAt(LocalDateTime.of(2026, 4, 18, 0, 0))
                .build();
        SoptLetterInfo.TopicListResult expected = SoptLetterInfo.TopicListResult.builder()
                .topics(List.of(topic))
                .build();
        when(soptLetterService.getTopics()).thenReturn(expected);

        // when
        SoptLetterInfo.TopicListResult result = soptLetterFacade.getTopics();

        // then
        assertThat(result.getTopics()).hasSize(1);
        assertThat(result.getTopics().get(0).getTopicId()).isEqualTo(3L);
        assertThat(result.getTopics().get(0).getTitle()).isEqualTo("36기 회고");
        verify(soptLetterService, times(1)).getTopics();
    }

    @Test
    @DisplayName("SUCCESS_솝레터 주제 단일 조회 파사드가 서비스 메서드를 정상 호출하고 반환한다")
    void SUCCESS_getTopic() {
        // given
        final Long topicId = 3L;
        SoptLetterInfo.TopicDetail expected = SoptLetterInfo.TopicDetail.builder()
                .topicId(topicId)
                .title("36기 회고")
                .active(true)
                .startedAt(LocalDateTime.of(2026, 4, 18, 0, 0))
                .endedAt(LocalDateTime.of(2026, 4, 28, 23, 59, 59))
                .createdAt(LocalDateTime.of(2026, 4, 18, 0, 0))
                .build();
        when(soptLetterService.getTopic(eq(topicId))).thenReturn(expected);

        // when
        SoptLetterInfo.TopicDetail result = soptLetterFacade.getTopic(topicId);

        // then
        assertThat(result.getTopicId()).isEqualTo(topicId);
        assertThat(result.getTitle()).isEqualTo("36기 회고");
        assertThat(result.getActive()).isTrue();
        verify(soptLetterService, times(1)).getTopic(eq(topicId));
    }

    @Test
    @DisplayName("SUCCESS_메시지 작성 파사드가 서비스 메서드를 정상 호출하고 반환한다")
    void SUCCESS_createSoptLetter() {
        // given
        final Long userId = 1L;
        final Long topicId = 3L;
        final String content = "편지 내용";

        SoptLetterInfo.MessageResult expected = SoptLetterInfo.MessageResult.builder()
                .messageId(125L)
                .topicId(topicId)
                .authorNickname("반짝이는 고래")
                .content(content)
                .colorCode("#CCFFEC")
                .rotationDegree(4.0)
                .shapeType("POINT")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .likeCount(0)
                .likedByMe(false)
                .mine(true)
                .build();

        when(soptLetterService.createSoptLetter(eq(userId), eq(topicId), eq(content))).thenReturn(expected);

        // when
        SoptLetterInfo.MessageResult result = soptLetterFacade.createSoptLetter(userId, topicId, content);

        // then
        assertThat(result.getMessageId()).isEqualTo(125L);
        assertThat(result.getContent()).isEqualTo(content);
        assertThat(result.getAuthorNickname()).isEqualTo("반짝이는 고래");
        verify(soptLetterService, times(1)).createSoptLetter(eq(userId), eq(topicId), eq(content));
    }

    @Test
    @DisplayName("SUCCESS_메시지 수정 파사드가 서비스 메서드를 정상 호출하고 반환한다")
    void SUCCESS_updateSoptLetter() {
        // given
        final Long userId = 1L;
        final Long topicId = 3L;
        final Long messageId = 125L;
        final String content = "수정된 편지 내용";

        SoptLetterInfo.MessageResult expected = SoptLetterInfo.MessageResult.builder()
                .messageId(messageId)
                .topicId(topicId)
                .authorNickname("반짝이는 고래")
                .content(content)
                .colorCode("#CCFFEC")
                .rotationDegree(4.0)
                .shapeType("POINT")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .likeCount(0)
                .likedByMe(false)
                .mine(true)
                .build();

        when(soptLetterService.updateSoptLetter(eq(userId), eq(topicId), eq(messageId), eq(content))).thenReturn(expected);

        // when
        SoptLetterInfo.MessageResult result = soptLetterFacade.updateSoptLetter(userId, topicId, messageId, content);

        // then
        assertThat(result.getMessageId()).isEqualTo(messageId);
        assertThat(result.getContent()).isEqualTo(content);
        assertThat(result.getAuthorNickname()).isEqualTo("반짝이는 고래");
        verify(soptLetterService, times(1)).updateSoptLetter(eq(userId), eq(topicId), eq(messageId), eq(content));
    }

    @Test
    @DisplayName("SUCCESS_메시지 삭제 파사드가 서비스 메서드를 정상 호출한다")
    void SUCCESS_deleteSoptLetter() {
        // given
        final Long userId = 1L;
        final Long topicId = 3L;
        final Long messageId = 125L;

        // when
        soptLetterFacade.deleteSoptLetter(userId, topicId, messageId);

        // then
        verify(soptLetterService, times(1)).deleteSoptLetter(eq(userId), eq(topicId), eq(messageId));
    }
}
