package org.sopt.app.application.mission;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.mission.MissionInfo.AppjamMissionInfo;
import org.sopt.app.domain.entity.AppjamUser;
import org.sopt.app.domain.entity.soptamp.Mission;
import org.sopt.app.domain.entity.soptamp.SoptampUser;
import org.sopt.app.domain.entity.soptamp.Stamp;
import org.sopt.app.domain.enums.TeamNumber;
import org.sopt.app.interfaces.postgres.AppjamUserRepository;
import org.sopt.app.interfaces.postgres.MissionRepository;
import org.sopt.app.interfaces.postgres.SoptampUserRepository;
import org.sopt.app.interfaces.postgres.StampRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppjamMissionService {

    private final AppjamUserRepository appjamUserRepository;
    private final MissionRepository missionRepository;
    private final StampRepository stampRepository;
    private final SoptampUserRepository soptampUserRepository;


    public List<AppjamMissionInfo> getAllMissions(TeamNumber teamNumber) {
        val userIds = getTeamUserIds(teamNumber);
        val stampsByMissionId = getStampMapByUserIds(userIds);
        val soptampUserByUserId = getSoptampUserMapByUserIds(userIds);
        val displayedMissions = missionRepository.findAllByDisplay(true);

        return displayedMissions.stream()
            .map(mission -> toTeamMissionInfo(mission, stampsByMissionId, soptampUserByUserId))
            .toList();
    }

    public List<AppjamMissionInfo> getMissionsByCondition(TeamNumber teamNumber,
        boolean isCompleted) {
        List<AppjamMissionInfo> allMissions = getAllMissions(teamNumber);

        return allMissions.stream()
            .filter(mission -> Objects.equals(mission.isCompleted(), isCompleted))
            .toList();
    }

    private AppjamMissionInfo toTeamMissionInfo(
        Mission mission,
        Map<Long, Stamp> stampByMissionId,
        Map<Long, SoptampUser> soptampUserByUserId
    ) {
        val stamp = Optional.ofNullable(stampByMissionId.get(mission.getId()));
        val ownerName = stamp.map(Stamp::getUserId)
            .map(soptampUserByUserId::get)
            .map(SoptampUser::getNickname)
            .orElse(null);
        return AppjamMissionInfo.of(mission, stamp.isPresent(), ownerName);
    }

    private List<Long> getTeamUserIds(TeamNumber teamNumber) {
        return appjamUserRepository.findAllByTeamNumber(teamNumber).stream()
            .map(AppjamUser::getUserId)
            .toList();
    }

    private Map<Long, Stamp> getStampMapByUserIds(Collection<Long> userIds) {
        return stampRepository.findAllByUserIdIn(userIds).stream()
            .collect(Collectors.toMap(
                Stamp::getMissionId,
                Function.identity(),
                (exist, replace) -> exist));
    }

    private Map<Long, SoptampUser> getSoptampUserMapByUserIds(Collection<Long> userIds) {
        return soptampUserRepository.findAllByUserIdIn(userIds).stream()
            .collect(Collectors.toMap(SoptampUser::getUserId, Function.identity()));
    }
}
