package org.sopt.app.application.soptamp;

import static java.util.function.UnaryOperator.identity;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.soptamp.SoptampUser;
import org.sopt.app.domain.enums.Part;
import org.sopt.app.interfaces.postgres.SoptampUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SoptampUserFinder {

    private final SoptampUserRepository soptampUserRepository;

    @Value("${sopt.current.generation}")
    private Long currentGeneration;

    public List<SoptampUserInfo> findAllOfCurrentGeneration() {
        return soptampUserRepository.findAllByGeneration(currentGeneration)
                .stream()
                .map(SoptampUserInfo::of)
                .toList();
    }

    public List<SoptampUserInfo> findAllByPartAndCurrentGeneration(Part part) {
        return soptampUserRepository.findAllByNicknameStartingWithAndGeneration(part.getPartName(), currentGeneration)
                .stream()
                .map(SoptampUserInfo::of)
                .toList();
    }

    public SoptampUserInfo findByNickname(String nickname) {
        SoptampUser soptampUser = soptampUserRepository.findUserByNickname(nickname)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        return SoptampUserInfo.of(soptampUser);
    }

    public SoptampUserInfo findById(Long userId) {
        SoptampUser soptampUser = soptampUserRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        return SoptampUserInfo.of(soptampUser);
    }

    @Transactional(readOnly = true)
    public Map<Long, SoptampUserInfo> findUserInfosByIdsAsMap(List<Long> userIds) {

        return soptampUserRepository.findAllByUserIdIn(userIds).stream()
                .map(SoptampUserInfo::of)
                .collect(java.util.stream.Collectors.toMap(
                        SoptampUserInfo::getUserId,
                        identity(),
                        (a, b) -> a,
                        java.util.LinkedHashMap::new
                ));
    }
}
