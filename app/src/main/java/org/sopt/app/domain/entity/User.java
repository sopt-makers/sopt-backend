package org.sopt.app.domain.entity;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.sopt.app.domain.enums.Authority;
import org.sopt.app.domain.enums.OsType;

import javax.persistence.*;

@Entity
@Table(name = "NEW_USER")
@Getter
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long id;

  @Column
  private String email;

  @Column
  @Enumerated(EnumType.STRING)
  private Authority auth;

  @Column
  private String clientToken;

  @Column
  @Enumerated(EnumType.STRING)
  private OsType osType;

  @Column
  private String password;

  @Column
  private String salt;

  @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
  private List<Stamp> stamps = new ArrayList<>();
}
