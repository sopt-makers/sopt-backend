package org.sopt.app.application.mission;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.mission.MissionInfo.TeamMissionInfo;
import org.sopt.app.domain.entity.TeamInfo;
import org.sopt.app.domain.entity.soptamp.Mission;
import org.sopt.app.domain.entity.soptamp.SoptampUser;
import org.sopt.app.domain.entity.soptamp.Stamp;
import org.sopt.app.domain.enums.TeamNumber;
import org.sopt.app.interfaces.postgres.MissionRepository;
import org.sopt.app.interfaces.postgres.SoptampUserRepository;
import org.sopt.app.interfaces.postgres.StampRepository;
import org.sopt.app.interfaces.postgres.TeamInfoRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamMissionService {

    private final TeamInfoRepository teamInfoRepository;
    private final MissionRepository missionRepository;
    private final StampRepository stampRepository;
    private final SoptampUserRepository soptampUserRepository;

    public List<MissionInfo.TeamMissionInfo> getAllMissions(TeamNumber teamNumber) {
        List<Mission> displayedMissions = missionRepository.findAllByDisplay(true);

        val userIds = getTeamUserIds(teamNumber);
        val stampsByMissionId = getStampsByMissionId(userIds);
        val soptampUserByUserId = getSoptampUserByUserId(userIds);

        return displayedMissions.stream()
            .map(mission -> {
                Optional<Stamp> stamp = Optional.ofNullable(stampsByMissionId.get(mission.getId()));
                Optional<String> ownerName = stamp.map(Stamp::getUserId)
                    .map(soptampUserByUserId::get)
                    .map(SoptampUser::getNickname);
                return TeamMissionInfo.of(mission, stamp.isPresent(), ownerName);
            })
            .toList();
    }

    private List<Long> getTeamUserIds(TeamNumber teamNumber) {
        return teamInfoRepository.findAllByTeamNumber(teamNumber).stream()
            .map(TeamInfo::getUserId)
            .toList();
    }

    private Map<Long, Stamp> getStampsByMissionId(Collection<Long> userIds) {
        return stampRepository.findAllByUserIdIn(userIds).stream()
            .collect(Collectors.toMap(Stamp::getMissionId, Function.identity()));
    }

    private Map<Long, SoptampUser> getSoptampUserByUserId(Collection<Long> userIds) {
        return soptampUserRepository.findAllByUserIdIn(userIds).stream()
            .collect(Collectors.toMap(SoptampUser::getUserId, Function.identity()));

    }
}
