package org.sopt.app.domain.entity;


import com.vladmihalcea.hibernate.type.array.ListArrayType;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Entity
@Table(name = "MISSION")
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
  private Long level;

  @Column
  private boolean display;

  @Type(type = "list-array")
  @Column(
      name = "profile_image",
      columnDefinition = "text[]"
  )
  private List<String> profileImage;

  @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL)
  private List<Stamp> stamps = new ArrayList<>();


}
