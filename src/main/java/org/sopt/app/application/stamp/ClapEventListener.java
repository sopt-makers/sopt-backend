package org.sopt.app.application.stamp;

import org.sopt.app.interfaces.postgres.ClapMilestoneGuard;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.mission.MissionService;
import org.sopt.app.application.platform.PlatformService;
import org.sopt.app.application.platform.dto.PlatformUserInfoResponse;
import org.sopt.app.application.soptamp.SoptampUserFinder;
import org.sopt.app.common.exception.NotFoundException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.common.utils.HttpHeadersUtils;
import org.sopt.app.presentation.poke.PokeResponse;
import org.sopt.app.presentation.stamp.ClapRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class ClapEventListener {

    private final RestTemplate restTemplate = new RestTemplate();
    private final HttpHeadersUtils headersUtils;

    private final StampService stampService;
    private final MissionService missionService;
    private final PlatformService platformService;
    private final SoptampUserFinder soptampUserFinder;

    private final ClapMilestoneGuard clapMilestoneGuard;

    @Value("${makers.push.server}")
    private String baseURI;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onClap(ClapEvent event) {
        final int oldClapTotal = event.getOldClapTotal();
        final int newClapTotal = event.getNewClapTotal();

        Long missionId = stampService.getMissionIdByStampId(event.getStampId());
        String missionTitle = missionService.getMissionTitleById(missionId);

        val ownerProfile = platformService.getPlatformUserInfoResponse(event.getOwnerUserId());
        String ownerName = ownerProfile.name();
        String ownerPart = Optional.ofNullable(ownerProfile.getLatestActivity())
                .map(PlatformUserInfoResponse.SoptActivities::part)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_PART_NOT_FOUND));
        String nickname = soptampUserFinder.findById(event.getOwnerUserId()).getNickname();

        if (crossed(oldClapTotal, newClapTotal, 1) && clapMilestoneGuard.tryMark(event.getStampId(), 1)) {
            send(ClapRequest.ClapAlarmRequest.of(event.getOwnerUserId(), missionTitle, nickname));
        }

        if (crossed(oldClapTotal, newClapTotal, 100)
                && clapMilestoneGuard.tryMark(event.getStampId(), 100)) {
            send(ClapRequest.ClapAlarmRequest.of(event.getOwnerUserId(), 100, missionTitle, ownerName, ownerPart, nickname));
        } else if (crossed(oldClapTotal, newClapTotal, 500)
                && clapMilestoneGuard.tryMark(event.getStampId(), 500)) {
            send(ClapRequest.ClapAlarmRequest.of(event.getOwnerUserId(), 500, missionTitle, ownerName, ownerPart, nickname));
        }

        // 한 번에 여러 구간(2000, 3000)을 넘어도 낮은 것만 처리
        for (int k = 1000; k <= 10000; k += 1000) {
            if (crossed(oldClapTotal, newClapTotal, k)
                    && clapMilestoneGuard.tryMark(event.getStampId(), k)) {
                send(ClapRequest.ClapAlarmRequest.of(k, missionTitle, nickname));
                break;
            }
        }
    }

    private boolean crossed(int oldTotal, int newTotal, int threshold) {
        return oldTotal < threshold && newTotal >= threshold;
    }

    private void send(ClapRequest.ClapAlarmRequest body) {
        val entity = new HttpEntity<>(body, headersUtils.createHeadersForSend());
        restTemplate.exchange(
                baseURI,
                HttpMethod.POST,
                entity,
                PokeResponse.PokeAlarmStatusResponse.class
        );
    }
}
