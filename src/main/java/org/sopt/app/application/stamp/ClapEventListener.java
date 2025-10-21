package org.sopt.app.application.stamp;

import org.sopt.app.domain.enums.SoptPart;
import org.sopt.app.interfaces.postgres.ClapMilestoneGuard;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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

        AlarmData alarmData = new AlarmData(event);

        if (crossed(oldClapTotal, newClapTotal, 1) && clapMilestoneGuard.tryMarkFirstHit(event.getStampId(), 1)) {
            sendWhenClapFirst(alarmData);
        }

        if (crossed(oldClapTotal, newClapTotal, 100)
            && clapMilestoneGuard.tryMarkFirstHit(event.getStampId(), 100)) {
            sendWhenClap100Or500(alarmData, 100);
        } else if (crossed(oldClapTotal, newClapTotal, 500)
            && clapMilestoneGuard.tryMarkFirstHit(event.getStampId(), 500)) {
            sendWhenClap100Or500(alarmData, 500);
        }

        // 한 번에 여러 구간(2000, 3000)을 넘어도 낮은 것만 처리
        // 정책상 상한 10000까지 발송 (필요 시 조정)
        for (int k = 1000; k <= 10000; k += 1000) {
            if (crossed(oldClapTotal, newClapTotal, k)
                && clapMilestoneGuard.tryMarkFirstHit(event.getStampId(), k)) {
                sendWhenClapKilo(alarmData, k);
                break;
            }
        }
    }

    private void sendWhenClapFirst(AlarmData alarmData){
        send(ClapRequest.ClapAlarmRequest.ofOwnerClapFirst(
            alarmData.getOwnerUserId(),
            alarmData.getStampId(),
            alarmData.getMissionTitle(),
            alarmData.getOwnerPart(),
            alarmData.getOwnerNickname()));
    }

    private void sendWhenClap100Or500(AlarmData alarmData, int mileStone){
        send(ClapRequest.ClapAlarmRequest.ofOwnerClap100Or500(
            alarmData.getOwnerUserId(),
            alarmData.getStampId(),
            mileStone,
            alarmData.getMissionTitle(),
            alarmData.getOwnerName(),
            alarmData.getOwnerPart(),
            alarmData.getOwnerNickname()));
    }

    private void sendWhenClapKilo(AlarmData alarmData, int mileStone){
        send(ClapRequest.ClapAlarmRequest.ofOwnerClapKilo(
            alarmData.getOwnerUserId(),
            alarmData.getStampId(),
            mileStone,
            alarmData.getMissionTitle(),
            alarmData.getOwnerPart(),
            alarmData.getOwnerNickname()));
    }

    private boolean crossed(int oldTotal, int newTotal, int threshold) {
        return oldTotal < threshold && newTotal >= threshold;
    }

    private void send(ClapRequest.ClapAlarmRequest body) {
        try {
            val entity = new HttpEntity<>(body, headersUtils.createHeadersForSend());
            restTemplate.exchange(
                baseURI,
                HttpMethod.POST,
                entity,
                PokeResponse.PokeAlarmStatusResponse.class
            );
        } catch (Exception e) {
            log.warn("Clap alarm send failed: body={}, err={}", body, e.toString());
            throw e; // 재시도 위해 그대로 throw
        }
    }

    private class AlarmData {

        private final ClapEvent event;

        private OwnerInfo ownerInfo;
        private MissionInfo missionInfo;

        public AlarmData(ClapEvent clapEvent) {
            this.event = clapEvent;
        }

        public ClapEvent getEvent() {
            return event;
        }

        public OwnerInfo getOwnerInfo() {
            if(this.ownerInfo == null){
                return fetchOwnerInfo(getEvent());
            }
            return this.ownerInfo;
        }

        public MissionInfo getMissionInfo() {
            if(this.missionInfo == null){
                return fetchMissionInfo(getEvent());
            }
            return this.missionInfo;
        }

        public Long getOwnerUserId() {
            return getEvent().getOwnerUserId();
        }

        public Long getStampId(){
            return getEvent().getStampId();
        }

        public String getOwnerName() {
            return getOwnerInfo().name();
        }

        public SoptPart getOwnerPart() {
            return getOwnerInfo().part();
        }

        public String getOwnerNickname() {
            return getOwnerInfo().nickname();
        }

        public Long getMissionId() {
            return getMissionInfo().id();
        }

        public String getMissionTitle() {
            return getMissionInfo().title();
        }


        private OwnerInfo fetchOwnerInfo(ClapEvent clapEvent) {
            val ownerProfile = platformService.getPlatformUserInfoResponse(clapEvent.getOwnerUserId());
            String ownerName = ownerProfile.name();
            String ownerPartName = Optional.ofNullable(ownerProfile.getLatestActivity())
                .map(PlatformUserInfoResponse.SoptActivities::part)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_PART_NOT_FOUND));
            SoptPart ownerPart = SoptPart.findSoptPartByPartName(ownerPartName);
            String nickname = soptampUserFinder.findById(clapEvent.getOwnerUserId()).getNickname();

            return new OwnerInfo(ownerName, ownerPart, nickname);
        }

        private MissionInfo fetchMissionInfo(ClapEvent clapEvent){
            Long missionId = stampService.getMissionIdByStampId(clapEvent.getStampId());
            String missionTitle = missionService.getMissionTitleById(missionId);

            return new MissionInfo(missionId, missionTitle);
        }

    }

    private record OwnerInfo(String name, SoptPart part, String nickname) {}
    private record MissionInfo(Long id, String title) {}

}
