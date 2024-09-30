package org.sopt.app.application.soptamp;

import static org.sopt.app.domain.enums.PlaygroundPart.findPlaygroundPart;

import java.util.*;
import lombok.*;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.soptamp.SoptampUser;
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
        SoptampUser user = soptampUserRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        return SoptampUserInfo.of(user);
    }

    @Transactional
    public SoptampUserInfo editProfileMessage(Long userId, String profileMessage) {
        SoptampUser soptampUser = soptampUserRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        soptampUser.updateProfileMessage(profileMessage);
        return SoptampUserInfo.of(soptampUser);
    }

    @Transactional
    public Long createSoptampUser(String name, Long userId) {
        Optional<SoptampUser> registeredUser = soptampUserRepository.findByUserId(userId);
        if (registeredUser.isEmpty()) {
            val newSoptampUser = this.createNewSoptampUser(name, userId);
            return soptampUserRepository.save(newSoptampUser).getId();
        }
        return registeredUser.get().getId();
    }

    private SoptampUser createNewSoptampUser(String name, Long userId) {
        return SoptampUser.builder()
                .userId(userId)
                .nickname(name)
                .profileMessage("")
                .totalPoints(0L)
                .build();
    }

    @Transactional
    public void addPointByLevel(Long userId, Integer level) {
        SoptampUser soptampUser = soptampUserRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        soptampUser.addPointsByLevel(level);
    }

    @Transactional
    public void subtractPointByLevel(Long userId, Integer level) {
        SoptampUser soptampUser = soptampUserRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        soptampUser.subtractPointsByLevel(level);
    }

    @Transactional
    public void initPoint(Long userId) {
        SoptampUser soptampUser = soptampUserRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        soptampUser.initTotalPoints();
        soptampUserRepository.save(soptampUser);
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