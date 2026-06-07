package org.sopt.app.application.soptletter;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.sopt.app.application.soptletter.SoptLetterInfo.Profile;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.exception.ConflictException;
import org.sopt.app.common.exception.NotFoundException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.common.utils.AnonymousNameGenerator;
import org.sopt.app.domain.entity.soptletter.SoptLetter;
import org.sopt.app.domain.entity.soptletter.SoptLetterProfile;
import org.sopt.app.interfaces.postgres.soptletter.SoptLetterLikeRepository;
import org.sopt.app.interfaces.postgres.soptletter.SoptLetterProfileRepository;
import org.sopt.app.interfaces.postgres.soptletter.SoptLetterRepository;
import org.sopt.app.interfaces.postgres.soptletter.SoptLetterTopicRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SoptLetterService {

    private static final int MAX_NICKNAME_RETRY_COUNT = 3;
    private static final int NICKNAME_CANDIDATE_SIZE = 3;
    private static final int DAILY_MESSAGE_LIMIT = 10;

    private final SoptLetterProfileRepository soptLetterProfileRepository;
    private final AnonymousNameGenerator anonymousNameGenerator;
    private final SoptLetterRepository soptLetterRepository;
    private final SoptLetterTopicRepository soptLetterTopicRepository;
    private final SoptLetterLikeRepository soptLetterLikeRepository;
    private final SoptLetterGenerator soptLetterGenerator;
    private final Clock clock;

    public boolean isOnboarded(Long userId) {
        return soptLetterProfileRepository.existsByUserId(userId);
    }

    @Transactional
    public Profile getOrCreateProfile(Long userId) {
        Optional<SoptLetterProfile> profileOpt = soptLetterProfileRepository.findByUserId(userId);
        if (profileOpt.isPresent()) {
            SoptLetterProfile profile = profileOpt.get();
            return Profile.from(profile);
        }

        String uniqueNickname = generateUniqueNickname();
        return createAndSaveProfile(userId, uniqueNickname);
    }

    private String generateUniqueNickname() {
        for (int attempt = 0; attempt < MAX_NICKNAME_RETRY_COUNT; attempt++) {
            List<String> candidates = anonymousNameGenerator.generateMultiple(NICKNAME_CANDIDATE_SIZE);
            Set<String> existingNicknames = soptLetterProfileRepository.findExistingNicknames(candidates);

            Optional<String> available = candidates.stream()
                    .filter(candidate -> !existingNicknames.contains(candidate))
                    .findFirst();

            if (available.isPresent()) {
                return available.get();
            }
        }
        log.error("솝레터 유니크 닉네임 생성 실패");
        throw new ConflictException(ErrorCode.SOPT_LETTER_NICKNAME_IS_FULL);
    }

    private Profile createAndSaveProfile(Long userId, String nickname) {
        try {
            SoptLetterProfile newProfile = SoptLetterProfile.of(userId, nickname);
            soptLetterProfileRepository.saveAndFlush(newProfile);
            return Profile.from(newProfile);
        } catch (DataIntegrityViolationException e) {
            log.error("솝레터 프로필 생성 중 유니크 제약 오류 발생.", e);
            throw new ConflictException(ErrorCode.CONFLICT);
        }
    }

    @Transactional
    public Profile completeOnboarding(Long userId) {
        SoptLetterProfile profile = soptLetterProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.SOPT_LETTER_PROFILE_NOT_FOUND));
        profile.completeOnboarding();
        return Profile.from(profile);
    }

    @Transactional
    public SoptLetterInfo.MessageResult createSoptLetter(Long userId, Long topicId, String content) {
        val topic = soptLetterTopicRepository.findById(topicId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ENTITY_NOT_FOUND));
        val profile = soptLetterProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.SOPT_LETTER_PROFILE_NOT_FOUND));

        validateDailyMessageLimit(profile.getId(), LocalDateTime.now(clock));

        val latestLetterOpt = soptLetterRepository.findFirstByTopicIdOrderByIdDesc(topicId);
        val latestColor = latestLetterOpt.map(SoptLetter::getColor).orElse(null);

        val newLetter = soptLetterGenerator.generate(profile.getId(), topicId, content, latestColor);

        val savedLetter = soptLetterRepository.save(newLetter);
        return SoptLetterInfo.MessageResult.of(savedLetter, profile.getNickname(), false, true);
    }

    private void validateDailyMessageLimit(Long profileId, LocalDateTime now) {
        val startOfDay = now.toLocalDate().atStartOfDay();
        val todayCount = soptLetterRepository.countByAuthorProfileIdAndCreatedAtAfter(profileId, startOfDay);
        if (todayCount >= DAILY_MESSAGE_LIMIT) {
            throw new BadRequestException(ErrorCode.SOPT_LETTER_DAILY_LIMIT_EXCEEDED);
        }
    }

    @Transactional
    public SoptLetterInfo.MessageResult updateSoptLetter(Long userId, Long messageId, String content) {
        val letter = soptLetterRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.SOPT_LETTER_NOT_FOUND));
        val profile = soptLetterProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.SOPT_LETTER_PROFILE_NOT_FOUND));

        letter.updateMessage(profile.getId(), content);

        val likedByMe = soptLetterLikeRepository.existsByLetterIdAndUserId(messageId, userId);
        return SoptLetterInfo.MessageResult.of(letter, profile.getNickname(), likedByMe, true);
    }
}

