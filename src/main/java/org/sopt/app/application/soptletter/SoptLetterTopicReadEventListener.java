package org.sopt.app.application.soptletter;

import static org.sopt.app.common.config.AsyncConfig.CACHE_SYNC_EXECUTOR;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.sopt.app.interfaces.postgres.soptletter.SoptLetterTopicReadRepository;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SoptLetterTopicReadEventListener {

    private final SoptLetterTopicReadRepository soptLetterTopicReadRepository;

    @Async(CACHE_SYNC_EXECUTOR)
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    @EventListener
    public void onTopicRead(SoptLetterTopicReadEvent event) {
        soptLetterTopicReadRepository.upsertLastReadLetterId(
            event.getUserId(), event.getTopicId(), event.getLastReadLetterId());
    }
}
