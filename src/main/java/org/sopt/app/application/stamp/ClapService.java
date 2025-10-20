package org.sopt.app.application.stamp;

import java.util.Objects;
import org.sopt.app.common.event.EventPublisher;
import java.util.Optional;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.exception.ForbiddenException;
import org.sopt.app.common.exception.NotFoundException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.soptamp.Clap;
import org.sopt.app.domain.entity.soptamp.Stamp;
import org.sopt.app.interfaces.postgres.ClapRepository;
import org.sopt.app.interfaces.postgres.StampRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClapService {

	private static final int MAX_RETRY = 3;

	private final EventPublisher eventPublisher;
	private final ClapRepository clapRepository;
	private final StampRepository stampRepository;

	/**
	 * 사용자(userId)가 스탬프(stampId)에 increment만큼 박수를 친다.
	 * - 자기 글이면 금지
	 * - Clap(유저별 1행)은 JPA + @Version (낙관적 락)으로 갱신, 재시도 최대 3회
	 * - Stamp 총합은 네이티브 원자 증가(RETURNING)
	 */
	@Transactional
	public int addClap(Long userId, Long stampId, int increment) {
		if (increment <= 0)
			throw new BadRequestException(ErrorCode.INVALID_CLAP_COUNT);

		// 1) 스탬프 조회 + 자기 글 금지
		Stamp stamp = stampRepository.findById(stampId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.STAMP_NOT_FOUND));
		if (Objects.equals(stamp.getUserId(), userId))
			throw new ForbiddenException(ErrorCode.SELF_CLAP_FORBIDDEN);

		// 2) Clap upsert (낙관적 락 + 재시도). 실제 적용된 양(applied)을 계산
		int applied = upsertUserClapWithRetry(userId, stampId, increment);

		// 3) 총합 반영 (네이티브 RETURNING) — applied가 0이면 스킵
		if (applied > 0) {
			stampRepository.incrementClapCountReturning(stampId, applied);

			eventPublisher.raise(ClapEvent.of(stamp.getUserId(), stampId));
		}
		return applied;
	}

    public Optional<Clap> getClap(Long userId, Long stampId) {
        return clapRepository.findByUserIdAndStampId(userId, stampId);
    }

    public int getUserClapCount(Long userId, Long stampId) {
        Optional<Clap> clap = getClap(userId, stampId);

        return clap.map(Clap::getClapCount).orElse(0);
    }

	/**
	 * Clap(유저별 1행) 업데이트.
	 * - 없으면 생성하고, 있으면 도메인 메서드로 상한(50) 컷팅
	 * - @Version 충돌 시 최대 MAX_RETRY 재시도
	 * - 반환: 이번 요청으로 실제로 적용된 증가량(applied)
	 */
	private int upsertUserClapWithRetry(Long userId, Long stampId, int requested) {
		int attempt = 0;
		while (true) {
			attempt++;
			try {
				Clap clap = clapRepository.findByUserIdAndStampId(userId, stampId)
					.orElseGet(() -> createClapSafely(userId, stampId));

				int applied = clap.incrementClapCount(requested); // 도메인에서 0..50 컷팅
				if (applied <= 0) return 0;

				clapRepository.saveAndFlush(clap); // @Version 체크
				return applied;

			} catch (ObjectOptimisticLockingFailureException e) {
				if (attempt >= MAX_RETRY) throw e;
			}
		}
	}

	/**
	 * (stamp_id, user_id) 유니크 제약 하에서 생성 경합을 안전 처리:
	 * - 없으면 생성
	 * - 동시 경합으로 유니크 예외시 재조회
	 */
	private Clap createClapSafely(Long userId, Long stampId) {
		try {
			Clap fresh = Clap.builder()
				.userId(userId)
				.stampId(stampId)
				.clapCount(0)
				.build();
			return clapRepository.saveAndFlush(fresh);
		} catch (DataIntegrityViolationException e) {
			return clapRepository.findByUserIdAndStampId(userId, stampId)
				.orElseThrow(() -> e);
		}
	}
}
