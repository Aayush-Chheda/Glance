package com.glance.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false,nullable = false)
    private long userRoleId;

    @ManyToOne
    @JoinColumn(name="user_id")
    @JsonIgnore
    private AppUser appUser;

    @ManyToOne
    private Role role;
}
