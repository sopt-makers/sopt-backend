package org.sopt.app.application.soptamp;

import static org.sopt.app.domain.enums.PlaygroundPart.findPlaygroundPart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.SoptampUser;
import org.sopt.app.domain.enums.Part;
import org.sopt.app.domain.enums.PlaygroundPart;
import org.sopt.app.interfaces.postgres.SoptampUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SoptampUserService {

    private final SoptampUserRepository soptampUserRepository;

    @Transactional(readOnly = true)
    public SoptampUserInfo getSoptampUserInfo(Long userId) {
        val user = soptampUserRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND.getMessage()));
        return SoptampUserInfo.builder()
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
    public SoptampUserInfo editNickname(SoptampUserInfo soptampUserInfo, String nickname) {
        val newSoptampUser = SoptampUser.builder()
                .id(soptampUserInfo.getId())
                .userId(soptampUserInfo.getUserId())
                .profileMessage(soptampUserInfo.getProfileMessage())
                .totalPoints(soptampUserInfo.getTotalPoints())
                .nickname(nickname)
                .build();
        soptampUserRepository.save(newSoptampUser);
        return SoptampUserInfo.builder()
                .id(newSoptampUser.getId())
                .userId(newSoptampUser.getUserId())
                .profileMessage(newSoptampUser.getProfileMessage())
                .totalPoints(newSoptampUser.getTotalPoints())
                .nickname(newSoptampUser.getNickname())
                .build();
    }

    @Transactional
    public SoptampUserInfo editProfileMessage(SoptampUserInfo soptampUserInfo,
            String profileMessage) {
        val newSoptampUser = SoptampUser.builder()
                .id(soptampUserInfo.getId())
                .userId(soptampUserInfo.getUserId())
                .profileMessage(profileMessage)
                .totalPoints(soptampUserInfo.getTotalPoints())
                .nickname(soptampUserInfo.getNickname())
                .build();
        return SoptampUserInfo.of(soptampUserRepository.save(newSoptampUser));
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

    public List<SoptampUser> findAllBySoptampUserIds(List<Long> userIdList) {
        return soptampUserRepository.findAllById(userIdList);
    }

    public List<SoptampUser> findAllSoptampUsers() {
        return soptampUserRepository.findAll();
    }

    public List<Long> findSoptampUserByPart(Part part) {
        return soptampUserRepository.findAllByNicknameStartingWith(part.getPartName())
                .stream()
                .map(SoptampUser::getId)
                .toList();
    }

    public SoptampUserInfo findSoptampUserByNickname(String nickname) {
        return SoptampUserInfo.of(
                soptampUserRepository.findUserByNickname(nickname)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND.getMessage()))
        );
    }

    @Transactional
    public SoptampUserInfo addPoint(Long userId, Integer level) {
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
        return SoptampUserInfo.of(soptampUserRepository.save(newSoptampUser));
    }

    @Transactional
    public SoptampUserInfo subtractPoint(Long userId, Integer level) {
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
        return SoptampUserInfo.of(soptampUserRepository.save(newSoptampUser));
    }

    public SoptampUserInfo findByNickname(String nickname) {
        val soptampUser = soptampUserRepository.findUserByNickname(nickname)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND.getMessage()));
        return SoptampUserInfo.of(soptampUser);
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

    @Transactional
    public void initAllSoptampUserPoints() {
        val soptampUserList = soptampUserRepository.findAll();
        soptampUserList.forEach(SoptampUser::initTotalPoints);
        soptampUserRepository.saveAll(soptampUserList);
    }

    public List<SoptampUser> getSoptampUserInfoList(List<Long> userIdList) {
        return soptampUserRepository.findAllByUserIdIn(userIdList);
    }

    @Transactional
    public List<SoptampUser> initAllCurrentGenerationSoptampUser(
            List<SoptampUser> soptampUserList,
            List<SoptampUserPlaygroundInfo> userInfoList
    ) {
        val validatedSoptampUserList = validateNickname(soptampUserList);

        validatedSoptampUserList.forEach(soptampUser -> {
            val userInfo = userInfoList.stream()
                    .filter(e -> soptampUser.getUserId().equals(e.getUserId()))
                    .findFirst().get();
            PlaygroundPart part = findPlaygroundPart(userInfo.getPart());
            soptampUser.updateGenerationAndPart(
                    userInfo.getGeneration(),
                    part
            );
        });
        soptampUserRepository.saveAll(validatedSoptampUserList);

        return validatedSoptampUserList;
    }

    @Transactional
    public List<SoptampUser> validateNickname(List<SoptampUser> soptampUserList) {
        // uniqueNickname map 생성
        val nicknameMap = generateUniqueNicknameMap(
                soptampUserList.stream().sorted(Comparator.comparing(SoptampUser::getNickname))
                        .map(SoptampUser::getNickname).toList());

        // soptampUser 리스트 userId 기준으로 중복 닉네임 알파벳 부여
        return updateUniqueNickname(soptampUserList, nicknameMap);
    }

    private HashMap<String, ArrayList<String>> generateUniqueNicknameMap(List<String> nicknameList) {
        val nicknameMap = new HashMap<String, ArrayList<String>>();
        val uniqueNicknameList = nicknameList.stream().distinct().toList();
        uniqueNicknameList.forEach(nickname -> {
            val count = Collections.frequency(nicknameList, nickname);
            if (count == 1) {
                nicknameMap.put(nickname, new ArrayList<>(List.of()));
            } else {
                val alphabetList = Arrays.asList("ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(0, count).split(""));
                val changedList = alphabetList.stream().map(alphabet -> nickname + alphabet).toList();
                nicknameMap.put(nickname, new ArrayList<>(changedList));
            }
        });
        return nicknameMap;
    }

    private List<SoptampUser> updateUniqueNickname(
            List<SoptampUser> soptampUserList,
            HashMap<String, ArrayList<String>> nicknameMap
    ) {
        soptampUserList.stream().sorted(Comparator.comparing(SoptampUser::getUserId)).forEach(soptampUser -> {
            val validatedNicknameList = nicknameMap.get(soptampUser.getNickname());
            if (!validatedNicknameList.isEmpty()) {
                val validatedNickname = validatedNicknameList.get(0);
                validatedNicknameList.remove(0);
                nicknameMap.put(soptampUser.getNickname(), validatedNicknameList);
                soptampUser.updateNickname(validatedNickname);
            }
        });
        return soptampUserList;
    }
}