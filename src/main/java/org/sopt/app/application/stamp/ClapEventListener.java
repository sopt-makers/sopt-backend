package org.sopt.app.application.stamp;

import jakarta.transaction.Transactional;
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

    @Value("${makers.push.server}")
    private String baseURI;

    @Async
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onClap(ClapEvent event) {
        int total = stampService.getStampClapCount(event.getStampId());
        Long missionId = stampService.getMissionIdByStampId(event.getStampId());

        String missionTitle = missionService.getMissionTitleById(missionId);

        val ownerProfile = platformService.getPlatformUserInfoResponse(event.getOwnerUserId());
        String ownerName = ownerProfile.name();
        String ownerPart = Optional.ofNullable(ownerProfile.getLatestActivity())
                .map(PlatformUserInfoResponse.SoptActivities::part)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_PART_NOT_FOUND));

        String nickname = soptampUserFinder.findById(event.getOwnerUserId()).getNickname();

        if (total == 1) {
            send(ClapRequest.ClapAlarmRequest.of(event.getOwnerUserId(), missionTitle, nickname));
        }

        if (total == 100) {
            send(ClapRequest.ClapAlarmRequest.of(event.getOwnerUserId(), 100, missionTitle, ownerName, ownerPart,
                    nickname));
        } else if (total == 500) {
            send(ClapRequest.ClapAlarmRequest.of(event.getOwnerUserId(), 500, missionTitle, ownerName, ownerPart,
                    nickname));
        }

        if (total >= 1000 && total <= 10000 && total % 1000 == 0) {
            send(ClapRequest.ClapAlarmRequest.of(total, missionTitle, nickname));
        }
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
