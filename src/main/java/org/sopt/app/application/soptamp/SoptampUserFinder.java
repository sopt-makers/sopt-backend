package org.sopt.app.application.soptamp;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.soptamp.SoptampUser;
import org.sopt.app.domain.enums.Part;
import org.sopt.app.interfaces.postgres.SoptampUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SoptampUserFinder {

    private final SoptampUserRepository soptampUserRepository;

    @Value("${sopt.current.generation}")
    private Long currentGeneration;

    public List<SoptampUserInfo> findAllBySoptampUserIds(List<Long> userIdList) {
        return soptampUserRepository.findAllById(userIdList)
                .stream()
                .map(SoptampUserInfo::of)
                .toList();
    }

    public List<SoptampUserInfo> findAllCurrentGenerationSoptampUsers() {
        return soptampUserRepository.findAllByGeneration(currentGeneration)
                .stream()
                .map(SoptampUserInfo::of)
                .toList();
    }

    public List<SoptampUserInfo> findSoptampUserIdByPart(Part part) {
        return soptampUserRepository.findAllByNicknameStartingWithAndGeneration(part.getPartName(), currentGeneration)
                .stream()
                .map(SoptampUserInfo::of)
                .toList();
    }

    public SoptampUserInfo findSoptampUserByNickname(String nickname) {
        return SoptampUserInfo.of(
                soptampUserRepository.findUserByNickname(nickname)
                        .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND.getMessage()))
        );
    }

    public SoptampUserInfo findByNickname(String nickname) {
        SoptampUser soptampUser = soptampUserRepository.findUserByNickname(nickname)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND.getMessage()));
        return SoptampUserInfo.of(soptampUser);
    }
}
