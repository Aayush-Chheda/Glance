package com.glance.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    private Integer id;
    private String name;
    private String caption;
    private String location;
    private int likes;
    private Date postedDate;
    private Integer userImageId;
    private List<Comment> commentList;
}
