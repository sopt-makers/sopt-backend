package org.sopt.app.application.soptletter;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.sopt.app.application.soptletter.SoptLetterInfo.Profile;
import org.sopt.app.application.soptletter.SoptLetterInfo.TopicMessageListResult;
import org.sopt.app.application.soptletter.SoptLetterInfo.TopicMessageSummary;
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
import org.springframework.data.domain.PageRequest;
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
        SoptLetterProfile profile = getProfileByUserId(userId);
        profile.completeOnboarding();
        return Profile.from(profile);
    }

    @Transactional(readOnly = true)
    public TopicMessageListResult getTopicMessages(Long userId, Long topicId, Long cursor, Integer size) {
        validateTopicMessagePageSize(size);

        val topic = soptLetterTopicRepository.findById(topicId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.ENTITY_NOT_FOUND));
        val requesterProfile = getProfileByUserId(userId);

        val fetchedLetters = getTopicLetters(topicId, cursor, size + 1);
        val hasNext = fetchedLetters.size() > size;
        val letters = hasNext ? fetchedLetters.subList(0, size) : fetchedLetters;
        val nextCursor = resolveNextCursor(letters);

        val likedLetterIds = findLikedLetterIds(userId, letters);
        val authorNicknamesByProfileId = getAuthorNicknamesByProfileId(letters);
        val messageSummaries = toTopicMessageSummaries(letters, requesterProfile, likedLetterIds, authorNicknamesByProfileId);
        val totalCount = Math.toIntExact(soptLetterRepository.countByTopicId(topicId));

        return TopicMessageListResult.of(topic, totalCount, nextCursor, hasNext, messageSummaries);
    }

    @Transactional
    public SoptLetterInfo.MessageResult createSoptLetter(Long userId, Long topicId, String content) {
        val topic = soptLetterTopicRepository.findById(topicId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ENTITY_NOT_FOUND));
        val profile = getProfileByUserId(userId);

        validateDailyMessageLimit(profile.getId(), LocalDateTime.now(clock));

        val latestLetterOpt = soptLetterRepository.findFirstByTopicIdOrderByIdDesc(topicId);
        val latestColor = latestLetterOpt.map(SoptLetter::getColor).orElse(null);

        val newLetter = soptLetterGenerator.generate(profile.getId(), topicId, content, latestColor);

        val savedLetter = soptLetterRepository.save(newLetter);
        return SoptLetterInfo.MessageResult.of(savedLetter, profile.getNickname(), false, true);
    }

    private void validateDailyMessageLimit(Long profileId, LocalDateTime now) {
        val startOfDay = now.toLocalDate().atStartOfDay();
        val todayCount = soptLetterRepository.countByAuthorProfileIdAndCreatedAtGreaterThanEqual(profileId, startOfDay);
        if (todayCount >= DAILY_MESSAGE_LIMIT) {
            throw new BadRequestException(ErrorCode.SOPT_LETTER_DAILY_LIMIT_EXCEEDED);
        }
    }

    @Transactional
    public SoptLetterInfo.MessageResult updateSoptLetter(Long userId, Long topicId, Long soptLetterId, String content) {
        val letter = getSoptLetter(soptLetterId);
        val profile = getProfileByUserId(userId);

        letter.validateInTopic(topicId);
        letter.updateMessage(profile.getId(), content);

        val likedByMe = soptLetterLikeRepository.existsByLetterIdAndUserId(soptLetterId, userId);
        return SoptLetterInfo.MessageResult.of(letter, profile.getNickname(), likedByMe, true);
    }

    @Transactional
    public void deleteSoptLetter(Long userId, Long topicId, Long soptLetterId) {
        val letter = getSoptLetter(soptLetterId);
        val profile = getProfileByUserId(userId);

        letter.validateDeletable(profile.getId());
        letter.validateInTopic(topicId);

        soptLetterLikeRepository.deleteAllByLetterIdInQuery(letter.getId());
        soptLetterRepository.delete(letter);
    }

    public SoptLetter getSoptLetter(Long soptLetterId) {
        return soptLetterRepository.findById(soptLetterId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.SOPT_LETTER_NOT_FOUND));
    }

    public SoptLetterProfile getProfileByUserId(Long userId) {
        return soptLetterProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.SOPT_LETTER_PROFILE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public SoptLetterInfo.MessageResult getMessageDetail(Long userId, Long topicId, Long messageId) {
        val profile = getProfileByUserId(userId);
        val letter = getSoptLetter(messageId);
        letter.validateInTopic(topicId);

        val mine = letter.isAuthor(profile.getId());
        String authorNickname = resolveAuthorNickname(letter, profile, mine);

        val likedByMe = soptLetterLikeRepository.existsByLetterIdAndUserId(messageId, userId);
        return SoptLetterInfo.MessageResult.of(letter, authorNickname, likedByMe, mine);
    }

    private void validateTopicMessagePageSize(Integer size) {
        if (size == null || size <= 0) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER);
        }
    }

    private List<SoptLetter> getTopicLetters(Long topicId, Long cursor, Integer size) {
        val pageable = PageRequest.of(0, size);
        if (cursor == null) {
            return soptLetterRepository.findAllByTopicIdOrderByIdDesc(topicId, pageable);
        }
        return soptLetterRepository.findAllByTopicIdAndIdLessThanOrderByIdDesc(topicId, cursor, pageable);
    }

    private Long resolveNextCursor(List<SoptLetter> letters) {
        if (letters.isEmpty()) {
            return null;
        }
        return letters.get(letters.size() - 1).getId();
    }

    private Set<Long> findLikedLetterIds(Long userId, List<SoptLetter> letters) {
        if (letters.isEmpty()) {
            return Set.of();
        }
        val letterIds = letters.stream()
            .map(SoptLetter::getId)
            .toList();
        return soptLetterLikeRepository.findLikedLetterIdsByUserIdAndLetterIdIn(userId, letterIds);
    }

    private Map<Long, String> getAuthorNicknamesByProfileId(List<SoptLetter> letters) {
        if (letters.isEmpty()) {
            return Map.of();
        }

        val authorProfileIds = letters.stream()
            .map(SoptLetter::getAuthorProfileId)
            .collect(Collectors.toSet());
        val authorNicknamesByProfileId = soptLetterProfileRepository.findAllById(authorProfileIds).stream()
            .collect(Collectors.toMap(SoptLetterProfile::getId, SoptLetterProfile::getNickname));

        if (authorNicknamesByProfileId.size() != authorProfileIds.size()) {
            throw new NotFoundException(ErrorCode.SOPT_LETTER_PROFILE_NOT_FOUND);
        }
        return authorNicknamesByProfileId;
    }

    private List<TopicMessageSummary> toTopicMessageSummaries(
        List<SoptLetter> letters,
        SoptLetterProfile requesterProfile,
        Set<Long> likedLetterIds,
        Map<Long, String> authorNicknamesByProfileId
    ) {
        return letters.stream()
            .map(letter -> TopicMessageSummary.of(
                letter,
                authorNicknamesByProfileId.get(letter.getAuthorProfileId()),
                likedLetterIds.contains(letter.getId()),
                letter.isAuthor(requesterProfile.getId())
            ))
            .toList();
    }

    private String resolveAuthorNickname(
        SoptLetter soptLetter,
        SoptLetterProfile requesterProfile,
        boolean mine
    ) {
        if (mine){
            return requesterProfile.getNickname();
        }
        return soptLetterProfileRepository.findById(soptLetter.getAuthorProfileId())
            .map(SoptLetterProfile::getNickname)
            .orElseThrow(() -> new NotFoundException(ErrorCode.SOPT_LETTER_PROFILE_NOT_FOUND));
    }

}
