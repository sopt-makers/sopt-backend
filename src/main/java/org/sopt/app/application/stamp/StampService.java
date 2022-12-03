package org.sopt.app.application.stamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.app.domain.entity.Mission;
import org.sopt.app.domain.entity.Stamp;
import org.sopt.app.interfaces.postgres.StampRepository;
import org.sopt.app.presentation.mission.dto.MissionRequestDto;
import org.sopt.app.presentation.stamp.dto.StampRequestDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StampService {

  private final StampRepository stampRepository;

  public Stamp findStamp(String userId, Long missionId) {
    return stampRepository.findByUserIdAndMissionId(Long.valueOf(userId), missionId);
  }

  @Transactional
  public Stamp uploadStamp(StampRequestDto stampRequestDto, List<String> imgPaths, String userId,
      Long missionId) {
    List<String> imgList = new ArrayList<>(imgPaths);
    Stamp stamp = this.convertStampImg(stampRequestDto, imgList, userId, missionId);
    return stampRepository.save(stamp);
  }


  //Stamp Entity 양식에 맞게 데이터 세팅
  private Stamp convertStampImg(StampRequestDto stampRequestDto, List<String> imgList,
      String userId, Long missionId) {
    return Stamp.builder()
        .contents(stampRequestDto.getContents())
        .createdAt(LocalDateTime.now())
        .images(imgList)
        .missionId(missionId)
        .userId(Long.valueOf(userId))
        .build();
  }

//  private Mission convertMission(MissionRequestDto missionRequestDto) {
//
//    return Mission.builder()
//        .title(missionRequestDto.getTitle())
//        .level(missionRequestDto.getLevel())
//        .display(true)
//        .build();
//  }

}
