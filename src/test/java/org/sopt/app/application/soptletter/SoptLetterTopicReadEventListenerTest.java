package org.sopt.app.application.soptletter;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.interfaces.postgres.soptletter.SoptLetterTopicReadRepository;

@ExtendWith(MockitoExtension.class)
class SoptLetterTopicReadEventListenerTest {

    @Mock
    private SoptLetterTopicReadRepository soptLetterTopicReadRepository;

    @InjectMocks
    private SoptLetterTopicReadEventListener soptLetterTopicReadEventListener;

    @Nested
    @DisplayName("읽음 기록")
    class OnTopicRead {

        @Nested
        @DisplayName("읽음 이벤트를 받으면 그 값 그대로 읽음 기록을 남긴다")
        class Upsert {

            @Test
            @DisplayName("SUCCESS_이벤트 값 그대로 읽음 기록 upsert를 부른다")
            void upsertsWithEventValues() {
                // given 유저 1이 3번 주제를 12번까지 읽었다는 이벤트다. 세 값을 모두 다르게 둬서 자리를 바꿔 넘기는 구현을 잡는다
                SoptLetterTopicReadEvent event = SoptLetterTopicReadEvent.of(1L, 3L, 12L);

                // when
                soptLetterTopicReadEventListener.onTopicRead(event);

                // then 읽음 기록 저장을 (1, 3, 12)로 한 번 부른다
                verify(soptLetterTopicReadRepository, times(1)).upsertLastReadLetterId(1L, 3L, 12L);
            }
        }
    }
}
