package com.glance.backend.repository;

import com.glance.backend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRoleRepository extends JpaRepository<Role,Long> {

    public Role findRoleByName(String name);
}
