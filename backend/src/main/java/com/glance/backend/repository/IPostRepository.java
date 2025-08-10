package com.glance.backend.repository;

import com.glance.backend.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPostRepository extends JpaRepository<Post,Long> {

    @Query("SELECT p FROM Post p order by p.postedDate DESC")
    public List<Post> findAll();

    @Query("SELECT p FROM Post p WHERE p.username=:username order by p.postedDate DESC")
    public List<Post> findPostByUsername(@Param("username") String username);

    @Query("SELECT p FROM Post p WHERE p.id=:x")
    public Post findPostById(@Param("x") Long id);

    @Modifying
    @Query("DELETE Post WHERE id=:x")
    public void deletePostById(@Param("x") Long id);
}
