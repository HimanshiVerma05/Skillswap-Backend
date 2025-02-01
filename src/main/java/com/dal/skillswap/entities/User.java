package com.dal.skillswap.entities;

import com.dal.skillswap.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Data
@Table(name = "user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String mobile;
    private String email;
    private UserRole role;
    private String pincode;
    private String password;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private PasswordResetToken passwordResetToken;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<UserSkill> userSkills;
    private Boolean isVerified;
    private String profilePictureName;
    private int age;
    private String description;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Post> posts;
    private String linkedInLink;
    private String instagramLink;
    private String youtubeLink;
    private Double latitude;
    private Double longitude;
    private Boolean enableNotification = true;
    private Boolean enableNotificationSounds = true;
    @OneToMany(mappedBy = "reviewee", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Review> receivedReviews;

    @OneToMany(mappedBy = "reviewer", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Review> givenReviews;

    public User() {

    }

    public User(String firstName, String lastName, String email, String mobile, UserRole role, String pincode, String password,
                Double latitude, Double longitude) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobile = mobile;
        this.role = role;
        this.pincode = pincode;
        this.password = password;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isVerified = false;
        this.profilePictureName = "";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setPasswordResetToken(PasswordResetToken passwordResetToken) {
        this.passwordResetToken = passwordResetToken;
        if (passwordResetToken != null) {
            passwordResetToken.setUser(this);
        }
    }
}
