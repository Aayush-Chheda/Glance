package com.glance.backend.model;

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
public class Role {

    private int roleId;
    private String name;
    private Set<UserRole> userRoles = new HashSet<>();
}
