package org.sopt.app.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.stamp.ClapService;
import org.sopt.app.common.event.EventPublisher;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.exception.ForbiddenException;
import org.sopt.app.common.exception.NotFoundException;
import org.sopt.app.domain.entity.soptamp.Clap;
import org.sopt.app.domain.entity.soptamp.Stamp;
import org.sopt.app.interfaces.postgres.ClapRepository;
import org.sopt.app.interfaces.postgres.StampRepository;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@ExtendWith(MockitoExtension.class)
class ClapServiceTest {

	@Mock ClapRepository clapRepository;
	@Mock StampRepository stampRepository;
	@InjectMocks ClapService clapService;
	@Mock EventPublisher eventPublisher;

	@Test
	void addClap_success_applies_fully_and_increments_stamp_total() {
		// given
		Long userId = 10L, stampId = 1L;
		Stamp stamp = Stamp.builder().id(stampId).userId(99L).clapCount(100).build(); // 작성자 != userId
		Clap clap = Clap.builder().stampId(stampId).userId(userId).clapCount(3).build();

		when(stampRepository.findById(stampId)).thenReturn(Optional.of(stamp));
		when(clapRepository.findByUserIdAndStampId(userId, stampId)).thenReturn(Optional.of(clap));
		when(clapRepository.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));
		when(stampRepository.incrementClapCountReturning(eq(stampId), eq(7)))
			.thenReturn(new StampRepository.StampCounts(107, 2L));

		// when
		int applied = clapService.addClap(userId, stampId, 7);

		// then
		assertThat(applied).isEqualTo(7);
		assertThat(clap.getClapCount()).isEqualTo(10);
		verify(stampRepository).incrementClapCountReturning(stampId, 7);
	}

	@Test
	void addClap_caps_at_50() {
		Long userId = 10L, stampId = 1L;
		Stamp stamp = Stamp.builder().id(stampId).userId(99L).clapCount(0).build();
		Clap clap = Clap.builder().stampId(stampId).userId(userId).clapCount(48).build();

		when(stampRepository.findById(stampId)).thenReturn(Optional.of(stamp));
		when(clapRepository.findByUserIdAndStampId(userId, stampId)).thenReturn(Optional.of(clap));
		when(clapRepository.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));
		when(stampRepository.incrementClapCountReturning(eq(stampId), eq(2)))
			.thenReturn(new StampRepository.StampCounts(2, 1L));

		int applied = clapService.addClap(userId, stampId, 7);

		assertThat(applied).isEqualTo(2);
		assertThat(clap.getClapCount()).isEqualTo(50);
		verify(stampRepository).incrementClapCountReturning(stampId, 2);
	}

	@Test
	void addClap_when_already_50_does_not_touch_stamp_total() {
		Long userId = 10L, stampId = 1L;
		Stamp stamp = Stamp.builder().id(stampId).userId(99L).clapCount(123).build();
		Clap clap = Clap.builder().stampId(stampId).userId(userId).clapCount(50).build();

		when(stampRepository.findById(stampId)).thenReturn(Optional.of(stamp));
		when(clapRepository.findByUserIdAndStampId(userId, stampId)).thenReturn(Optional.of(clap));

		int applied = clapService.addClap(userId, stampId, 5);

		assertThat(applied).isZero();
		verify(stampRepository, never()).incrementClapCountReturning(anyLong(), anyInt());
	}

	@Test
	void addClap_throws_when_self_clap() {
		Long userId = 10L, stampId = 1L;
		Stamp stamp = Stamp.builder().id(stampId).userId(userId).build(); // 자기 글

		when(stampRepository.findById(stampId)).thenReturn(Optional.of(stamp));

		assertThatThrownBy(() -> clapService.addClap(userId, stampId, 3))
			.isInstanceOf(ForbiddenException.class);
	}

	@Test
	void addClap_throws_when_invalid_increment() {
		assertThatThrownBy(() -> clapService.addClap(10L, 1L, 0))
			.isInstanceOf(BadRequestException.class);
		assertThatThrownBy(() -> clapService.addClap(10L, 1L, -5))
			.isInstanceOf(BadRequestException.class);
	}

	@Test
	void addClap_throws_when_stamp_not_found() {
		when(stampRepository.findById(1L)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> clapService.addClap(10L, 1L, 5))
			.isInstanceOf(NotFoundException.class);
	}

	@Test
	void addClap_retries_on_optimistic_lock_then_succeeds() {
		Long userId = 10L, stampId = 1L;
		Stamp stamp = Stamp.builder().id(stampId).userId(99L).build();
		Clap clap = Clap.builder().stampId(stampId).userId(userId).clapCount(0).build();

		when(stampRepository.findById(stampId)).thenReturn(Optional.of(stamp));
		when(clapRepository.findByUserIdAndStampId(userId, stampId)).thenReturn(Optional.of(clap));
		// 첫 저장은 락 예외, 두 번째는 성공
		when(clapRepository.saveAndFlush(any()))
			.thenThrow(new ObjectOptimisticLockingFailureException(Clap.class, 123L))
			.thenAnswer(inv -> inv.getArgument(0));
		when(stampRepository.incrementClapCountReturning(eq(stampId), eq(5)))
			.thenReturn(new StampRepository.StampCounts(5, 1L));

		int applied = clapService.addClap(userId, stampId, 5);

		assertThat(applied).isEqualTo(5);
		verify(clapRepository, times(2)).saveAndFlush(any());
		verify(stampRepository).incrementClapCountReturning(stampId, 5);
	}
}
