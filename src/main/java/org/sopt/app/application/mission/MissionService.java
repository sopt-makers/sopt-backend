package org.sopt.app.application.mission;

import java.util.Comparator;
import lombok.RequiredArgsConstructor;
import org.sopt.app.domain.entity.Mission;
import org.sopt.app.domain.entity.Stamp;
import org.sopt.app.interfaces.postgres.MissionRepository;
import org.sopt.app.interfaces.postgres.StampRepository;
import org.sopt.app.presentation.mission.dto.MissionRequestDto;
import org.sopt.app.presentation.mission.dto.GetAllMissionResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;
    private final StampRepository stampRepository;


    public List<GetAllMissionResponseDto> findAllMission(String userId) {
        List<Stamp> completedStamps = stampRepository.findAllByUserId(Long.parseLong(userId));
        List<Mission> missions = missionRepository.findAll();
        return missions.stream()
            .map(mission ->
                GetAllMissionResponseDto.builder()
                    .id(mission.getId())
                    .title(mission.getTitle())
                    .level(mission.getLevel())
                    .profileImage(mission.getProfileImage())
                    .isCompleted(isCompletedMission(mission.getId(), completedStamps))
                    .build())
            .sorted(Comparator.comparing(GetAllMissionResponseDto::getLevel))
            .collect(Collectors.toList());
    }

    private Boolean isCompletedMission(Long missionId, List<Stamp> completedStamps) {
        return completedStamps.stream().anyMatch(
                stamp -> stamp.getMissionId().equals(missionId));
    }


    // 게시글 작성 - 이미지 미포함
    @Transactional
    public Mission uploadMission(MissionRequestDto missionRequestDto) {
        Mission mission = this.convertMission(missionRequestDto);
        return missionRepository.save(mission);
    }

    // 게시글 작성 -  이미지 포함
    @Transactional
    public Mission uploadMissionWithImg(MissionRequestDto missionRequestDto, List<String> imgPaths) {

        List<String> imgList = new ArrayList<>(imgPaths);
        Mission mission = this.convertMissionImg(missionRequestDto, imgList);
        return missionRepository.save(mission);

    }


    //Mission 완료한 미션만 불러오기
    @Transactional
    public List<Mission> getCompleteMission(String userId) {

        //헤더에서 받은 userId로 Stamp 테이블에서 달성한 미션번호 가져오기
        List<Stamp> stampList = stampRepository.findAllByUserId(Long.valueOf(userId));

        //달성한 미션번호리스트
        List<Long> missionIdList = new ArrayList<>();
        for (Stamp stamp : stampList) {
            missionIdList.add(stamp.getMissionId());
        }

        return missionRepository.findMissionIn(missionIdList);
    }

//  private void getCompleteMission(List<Stamp> stampList, Mission mission) {
//
//    mission.
//
//
////    coupon.updateUseCoupon(
////        couponUsedCommand
////            .getCpnNoList()
////            .stream()
////            .filter((CouponUsed command) ->
////                command.getCouponNumber().equals(coupon.getCouponId()))
////            .findAny().orElseThrow(IllegalArgumentException::new));
//  }

    /**
     * 미완료 미션만 불러오기
     */
    @Transactional
    public List<Mission> getIncompleteMission(String userId) {

        //전체 미션 조회하기
        List<Mission> missionList = missionRepository.findAll();
        List<Long> allMissionList = new ArrayList<>();
        for (Mission mission : missionList) {
            allMissionList.add(mission.getId());
        }

        //stamp에서 userId로 달성한 mission 조회하기
        List<Stamp> stampList = stampRepository.findAllByUserId(Long.valueOf(userId));
        List<Long> completeMissionIdList = new ArrayList<>();
        for (Stamp stamp : stampList) {
            completeMissionIdList.add(stamp.getMissionId());
        }

        //두 리스트 비교해서 중복값 제거
        List<Long> inCompleteIdList = allMissionList.stream()
                .filter(all -> completeMissionIdList.stream().noneMatch(Predicate.isEqual(all)))
                .toList();

//    noneMatch: 중복 X
//    anyMatch: 중복 O
//    List<String> oldList = Arrays.asList("1", "2", "3", "4");
//    List<String> newList = Arrays.asList("3", "4", "5", "6");
//
//    List<String> resultList1 = oldList.stream()
//        .filter(old -> newList.stream().noneMatch(Predicate.isEqual(old)))
//        .collect(Collectors.toList());
//
//    System.out.println(resultList1); // [1, 2]

        return missionRepository.findMissionIn(inCompleteIdList);
    }


    //Mission Entity 양식에 맞게 데이터 세팅
    private Mission convertMissionImg(MissionRequestDto missionRequestDto, List<String> imgList) {
        return Mission.builder()
                .title(missionRequestDto.getTitle())
                .level(missionRequestDto.getLevel())
                .display(true)
                .profileImage(imgList)
                .build();
    }

    private Mission convertMission(MissionRequestDto missionRequestDto) {

        return Mission.builder()
                .title(missionRequestDto.getTitle())
                .level(missionRequestDto.getLevel())
                .display(true)
                .build();
    }

}
