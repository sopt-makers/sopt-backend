package org.sopt.app.domain.entity;


import com.vladmihalcea.hibernate.type.array.ListArrayType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import javax.persistence.*;

@Entity
@Table(name = "STAMP")
@TypeDef(
    name = "list-array",
    typeClass = ListArrayType.class
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stamp {

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
  private LocalDateTime createdAt;

  @Column
  private LocalDateTime updatedAt;

  //user_id 외래키  N:1
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  //mission_id 외래키 N:1
  @ManyToOne
  @JoinColumn(name = "mission_id")
  private Mission mission;

}
