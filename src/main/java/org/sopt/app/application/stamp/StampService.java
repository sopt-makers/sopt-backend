package org.sopt.app.application.stamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.app.domain.entity.Stamp;
import org.sopt.app.interfaces.postgres.StampRepository;
import org.sopt.app.presentation.stamp.dto.StampRequestDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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

  //사진 수정 할 경우
  @Transactional
  public Stamp editStampWithImg(StampRequestDto stampRequestDto, List<String> imgPaths,
      String userId,
      Long missionId) {

    Stamp stamp = stampRepository.findByUserIdAndMissionId(Long.valueOf(userId), missionId);

    if (StringUtils.hasText(stampRequestDto.getContents())) {
      stamp.changeContents(stampRequestDto.getContents());
    }
    stamp.changeImages(imgPaths);
    stamp.setUpdatedAt(LocalDateTime.now());

    return stampRepository.save(stamp);
  }


  //사진 수정 안할 경우
  @Transactional
  public Stamp editStampContents(StampRequestDto stampRequestDto, String userId,
      Long missionId) {

    Stamp stamp = stampRepository.findByUserIdAndMissionId(Long.valueOf(userId), missionId);

    if (StringUtils.hasText(stampRequestDto.getContents())) {
      stamp.changeContents(stampRequestDto.getContents());
    }

    stamp.setUpdatedAt(LocalDateTime.now());
    return stampRepository.save(stamp);
  }


  //Stamp 삭제 by stampId
  @Transactional
  public void deleteByStampId(Long stampId) {
    stampRepository.deleteById(stampId);
  }

  //Stamp 삭제 All by UserId
  @Transactional
  public void deleteStampByUserId(Long userId){
    stampRepository.deleteAllByUserId(userId);
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

}
