package org.sopt.app.domain.entity.soptamp;

import org.hibernate.annotations.Check;
import org.sopt.app.domain.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
	name = "clap",
	uniqueConstraints = @UniqueConstraint(
		name = "uk_clap_stamp_user",
		columnNames = {"stamp_id", "user_id"}
	)
)
@Check(constraints = "clap_count >= 0 AND clap_count <= 50")
public class Clap extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "stamp_id", nullable = false)
	private Long stampId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "clap_count", nullable = false)
	private int clapCount = 0;

	@Version
	private Long version;

	/**
	 * @param increment 요청된 증가값
	 * @return 실제로 적용된 증가값 (0 ~ increment)
	 */
	public int incrementClapCount(int increment) {
		if (increment <= 0) return 0;
		int before = this.clapCount;
		int next = before + increment;
		this.clapCount = Math.min(next, 50);
		return this.clapCount - before;
	}
}
