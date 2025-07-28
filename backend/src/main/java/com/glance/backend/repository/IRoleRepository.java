package com.glance.backend.repository;

import com.glance.backend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRoleRepository extends JpaRepository<Role,Long> {

    public Role findRoleByName(String name);
}
