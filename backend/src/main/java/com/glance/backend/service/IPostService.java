package com.glance.backend.service;

import com.glance.backend.model.AppUser;
import com.glance.backend.model.Post;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

public interface IPostService {

    public Post savePost(AppUser user, HashMap<String, String> request, String postImageName);

    public List<Post> postList();

    public Post getPostById(Long id);

    public List<Post> findPostByUsername(String username);

    public Post deletePost(Post post);

    public String savePostImage(MultipartFile multipartFile, String fileName);
}