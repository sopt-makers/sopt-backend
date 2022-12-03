package org.sopt.app.domain.entity;


import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "stamp", schema = "app_dev")
@TypeDef(
    name = "list-array",
    typeClass = ListArrayType.class
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stamp extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String contents;

  @Type(type = "list-array")
  @Column(
      columnDefinition = "text[]"
  )
  private List<String> images;

  @Column
  private LocalDate createdAt;

  @Column
  private LocalDate updatedAt;

  @Column
  private Long userId;

  @Column
  private Long missionId;
}
