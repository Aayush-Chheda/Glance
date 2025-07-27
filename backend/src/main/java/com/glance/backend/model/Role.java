package com.glance.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false,nullable = false)
    private int roleId;
    private String name;

    @OneToMany(mappedBy = "role",cascade = CascadeType.ALL)
    private Set<UserRole> userRoles = new HashSet<>();
}
