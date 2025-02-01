package com.dal.skillswap.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "userskill")
@AllArgsConstructor
@NoArgsConstructor
public class UserSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    @JsonBackReference
    private User user;
    @ManyToOne(targetEntity = Skill.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_Id")
    @JsonBackReference
    private Skill skill;

    public UserSkill(User user, Skill skill) {
        this.user = user;
        this.skill = skill;
    }
}
