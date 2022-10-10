package org.sopt.app.domain.entity;



import javax.persistence.*;

@Entity
@Table(name = "NOTICE")
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column()
    private String title;

    @Column
    private String contents;

    @Column
    private String images;

    @Column
    private String part;

    @Column
    private String creator;
}
