package com.fhsh.daitda.user.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "p_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
