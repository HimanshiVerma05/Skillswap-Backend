package com.dal.skillswap.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_Id")
    @JsonBackReference
    private User user;

    @ManyToOne(targetEntity = Skill.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_Id")
    @JsonBackReference
    private Skill skill;

    @Lob
    @Column(name = "content", length = 2000)
    private String content;

    @Lob
    private String image;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
