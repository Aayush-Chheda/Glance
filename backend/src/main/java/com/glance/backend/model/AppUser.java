package com.glance.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {

    private Integer id;
    private String name;
    private String username;
    private String password;
    private String email;
    private String bio;
    private Date createdDate;
    private Set<UserRole> userRoles = new HashSet<>();
    private List<Post> post;
    private List<Post> likedPost;

}
