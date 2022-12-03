package org.sopt.app.domain.entity;


import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "mission", schema = "app_dev")
@TypeDef(
    name = "list-array",
    typeClass = ListArrayType.class
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mission {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "mission_id")
  private Long id;

  @Column
  private String title;

  @Column(name = "level")
  private Integer level;

  @Column
  private boolean display;

  @Type(type = "list-array")
  @Column(
      name = "profile_image",
      columnDefinition = "text[]"
  )
  private List<String> profileImage;
}
