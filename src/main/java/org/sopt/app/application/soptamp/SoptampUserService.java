package org.sopt.app.application.soptamp;

import static org.sopt.app.domain.enums.PlaygroundPart.findPlaygroundPart;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.soptamp.SoptampPointInfo.Main;
import org.sopt.app.application.soptamp.SoptampPointInfo.Point;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.SoptampUser;
import org.sopt.app.domain.enums.Part;
import org.sopt.app.interfaces.postgres.SoptampUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SoptampUserService {

    private final SoptampUserRepository soptampUserRepository;

    @Transactional(readOnly = true)
    public SoptampUserInfo.SoptampUser getSoptampUserInfo(Long userId) {
        val user = soptampUserRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND.getMessage()));
        return SoptampUserInfo.SoptampUser.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .profileMessage(user.getProfileMessage())
                .totalPoints(user.getTotalPoints())
                .nickname(user.getNickname())
                .build();
    }

    @Transactional(readOnly = true)
    public void checkUserNickname(String nickname) {
        val nicknameUser = soptampUserRepository.findUserByNickname(nickname);
        if (nicknameUser.isPresent()) {
            throw new BadRequestException(ErrorCode.DUPLICATE_NICKNAME.getMessage());
        }
    }

    @Transactional
    public SoptampUserInfo.SoptampUser editNickname(SoptampUserInfo.SoptampUser soptampUser, String nickname) {
        val newSoptampUser = SoptampUser.builder()
                .id(soptampUser.getId())
                .userId(soptampUser.getUserId())
                .profileMessage(soptampUser.getProfileMessage())
                .totalPoints(soptampUser.getTotalPoints())
                .nickname(nickname)
                .build();
        soptampUserRepository.save(newSoptampUser);
        return SoptampUserInfo.SoptampUser.builder()
                .id(newSoptampUser.getId())
                .userId(newSoptampUser.getUserId())
                .profileMessage(newSoptampUser.getProfileMessage())
                .totalPoints(newSoptampUser.getTotalPoints())
                .nickname(newSoptampUser.getNickname())
                .build();
    }

    @Transactional
    public SoptampUserInfo.SoptampUser editProfileMessage(SoptampUserInfo.SoptampUser soptampUser,
            String profileMessage) {
        val newSoptampUser = SoptampUser.builder()
                .id(soptampUser.getId())
                .userId(soptampUser.getUserId())
                .profileMessage(profileMessage)
                .totalPoints(soptampUser.getTotalPoints())
                .nickname(soptampUser.getNickname())
                .build();
        soptampUserRepository.save(newSoptampUser);
        return SoptampUserInfo.SoptampUser.of(
                newSoptampUser.getId(),
                newSoptampUser.getUserId(),
                newSoptampUser.getProfileMessage(),
                newSoptampUser.getTotalPoints(),
                newSoptampUser.getNickname()
        );
    }

    @Transactional
    public Long updateSoptampUser(String name, Long userId) {
        val registerUser = soptampUserRepository.findByUserId(userId);
        if (registerUser.isEmpty()) {
            val newSoptampUser = SoptampUser.builder()
                    .userId(userId)
                    .profileMessage(null)
                    .totalPoints(0L)
                    .nickname(name)
                    .build();
            return soptampUserRepository.save(newSoptampUser).getId();

        }
        return registerUser.get().getId();
    }

    public List<Main> findRanks() {
        val userList = soptampUserRepository.findAll();
        return this.getRanking(userList);
    }

    public List<SoptampPointInfo.Main> findCurrentRanks(List<Point> soptampPointList) {
        val soptampUserIdList = soptampPointList.stream()
                .map(Point::getSoptampUserId).toList();
        val userList = soptampUserRepository.findAllById(soptampUserIdList);
        return this.getCurrentRanking(userList, soptampPointList);
    }

    private List<Main> getRanking(List<SoptampUser> userList) {
        val rankPoint = new AtomicInteger(1);
        return userList.stream().sorted(
                        Comparator.comparing(SoptampUser::getTotalPoints).reversed())
                .map(user -> Main.builder()
                        .rank(rankPoint.getAndIncrement())
                        .nickname(user.getNickname())
                        .point(user.getTotalPoints())
                        .profileMessage(user.getProfileMessage())
                        .build())
                .collect(Collectors.toList());
    }

    private List<Main> getCurrentRanking(List<SoptampUser> userList, List<Point> soptampPointList) {
        val rankPoint = new AtomicInteger(1);
        return soptampPointList.stream().sorted(Comparator.comparing(Point::getPoints).reversed())
                .map(point -> {
                    val user = userList.stream()
                            .filter(u -> u.getId().equals(point.getSoptampUserId()))
                            .findFirst()
                            .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND.getMessage()));
                    return Main.builder()
                            .rank(rankPoint.getAndIncrement())
                            .nickname(user.getNickname())
                            .point(point.getPoints())
                            .profileMessage(user.getProfileMessage())
                            .build();
                }).collect(Collectors.toList());
    }

    public List<Long> findSoptampUserByPart(Part part) {
        return soptampUserRepository.findAllByNicknameStartingWith(part.getPartName())
                .stream()
                .map(SoptampUser::getId)
                .toList();
    }

    public SoptampUser findRankByNickname(String nickname) {
        return soptampUserRepository.findUserByNickname(nickname)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND.getMessage()));
    }

    @Transactional
    public SoptampUserInfo.SoptampUser addPoint(Long userId, Integer level) {
        val user = soptampUserRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND.getMessage()));
        val newTotalPoint = user.getTotalPoints() + level;
        val newSoptampUser = SoptampUser.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .profileMessage(user.getProfileMessage())
                .totalPoints(newTotalPoint)
                .nickname(user.getNickname())
                .build();
        soptampUserRepository.save(newSoptampUser);
        return SoptampUserInfo.SoptampUser.builder()
                .id(newSoptampUser.getId())
                .userId(newSoptampUser.getUserId())
                .profileMessage(newSoptampUser.getProfileMessage())
                .totalPoints(newSoptampUser.getTotalPoints())
                .nickname(newSoptampUser.getNickname())
                .build();
    }

    @Transactional
    public SoptampUserInfo.SoptampUser subtractPoint(Long userId, Integer level) {
        val user = soptampUserRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND.getMessage()));
        val newTotalPoint = user.getTotalPoints() - level;
        val newSoptampUser = SoptampUser.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .profileMessage(user.getProfileMessage())
                .totalPoints(newTotalPoint)
                .nickname(user.getNickname())
                .build();
        soptampUserRepository.save(newSoptampUser);
        return SoptampUserInfo.SoptampUser.builder()
                .id(newSoptampUser.getId())
                .userId(newSoptampUser.getUserId())
                .profileMessage(newSoptampUser.getProfileMessage())
                .totalPoints(newSoptampUser.getTotalPoints())
                .nickname(newSoptampUser.getNickname())
                .build();
    }

    public SoptampUserInfo.SoptampUser findByNickname(String nickname) {
        val soptampUser = soptampUserRepository.findUserByNickname(nickname)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND.getMessage()));
        return SoptampUserInfo.SoptampUser.builder()
                .id(soptampUser.getId())
                .userId(soptampUser.getUserId())
                .profileMessage(soptampUser.getProfileMessage())
                .totalPoints(soptampUser.getTotalPoints())
                .nickname(soptampUser.getNickname())
                .build();
    }

    @Transactional
    public void initPoint(Long userId) {
        val soptampUser = soptampUserRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND.getMessage()));
        val newSoptampUser = SoptampUser.builder()
                .id(soptampUser.getId())
                .userId(soptampUser.getUserId())
                .profileMessage(soptampUser.getProfileMessage())
                .totalPoints(0L)
                .nickname(soptampUser.getNickname())
                .build();
        soptampUserRepository.save(newSoptampUser);
    }

    public void initAllSoptampUserPoints() {
        val soptampUserList = soptampUserRepository.findAll();
        soptampUserList.forEach(SoptampUser::initTotalPoints);
        soptampUserRepository.saveAll(soptampUserList);
    }

    public List<SoptampUser> getSoptampUserInfoList(List<Long> userIdList) {
        return soptampUserRepository.findAllByUserIdIn(userIdList);
    }

    public List<SoptampUser> initAllCurrentGenerationSoptampUser(
            List<SoptampUser> soptampUserList,
            List<SoptampUserInfo.SoptampUserPlaygroundInfo> userInfoList
    ) {
        soptampUserList.stream().forEach(soptampUser -> {
            val userInfo = userInfoList.stream()
                    .filter(e -> soptampUser.getUserId().equals(e.getUserId()))
                    .findFirst().get();
            soptampUser.updateGenerationAndPart(userInfo.getGeneration(), findPlaygroundPart(userInfo.getPart()));
        });
        soptampUserRepository.saveAll(soptampUserList);

        return soptampUserList;
    }

}
