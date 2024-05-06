package com.example.socialapplication.model.entity;

import com.example.socialapplication.model.entity.Enum.EnableType;
import com.example.socialapplication.model.entity.Enum.RoleType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.*;

@Entity
@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "users")
public class Users {
    @Id
    @Column(length = 36)
    @NotNull
    @EqualsAndHashCode.Include
    private String id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "gender")
    private boolean gender;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "date_of_birth")
    private Timestamp dateOfBirth;

    @Column(name = "mail")
    private String mail;

    @Column
    private String address;

    private String avatar;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private RoleType roleType;

    @Enumerated(EnumType.STRING)
    @Column(name = "enable")
    private EnableType enableType;

    public Users() {
        this.id = UUID.randomUUID().toString();
        this.enableType = EnableType.TRUE;
        this.roleType = RoleType.USER;
    }

    @Column(name = "created_at")
    private Timestamp createAt;

    @Column(name = "updated_at")
    private Timestamp updateAt;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "follows",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "following_user_id")
    )
    private List<Users> followingUser = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "favorites",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    private List<Posts> favoritesPost;

    @PrePersist
    protected void onCreate() {
        createAt = new Timestamp(new Date().getTime());
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = new Timestamp(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return "Users{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender=" + gender +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", mail='" + mail + '\'' +
                ", address='" + address + '\'' +
                ", avatar='" + avatar + '\'' +
                ", roleType=" + roleType +
                ", enableType=" + enableType +
                ", createAt=" + createAt +
                ", updateAt=" + updateAt +
                '}';
    }
}