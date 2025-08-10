package com.glance.backend.repository;

import com.glance.backend.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAppUserRepository  extends JpaRepository<AppUser,Long> {
    public AppUser findByUsername(String username);

    public AppUser findByEmail(String userEmail);

    @Query("SELECT appUser FROM AppUser appUser WHERE appUser.id=:x")
    public AppUser findUserById(@Param("x") Long id);

    public List<AppUser> findByUsernameContaining(String username);
}
