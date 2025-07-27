package com.glance.backend.model;

import jakarta.persistence.*;

@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false,nullable = false)
    private Integer id;

}
