package org.api.doit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * Entity class representing a User in the bd.
 * A user has a unique username, a password, and a list of tasks.
 */
@Entity
@Table(name = "users")
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "username", unique = true, nullable = false)
    @Setter
    private String username;

    @Column(name = "password", nullable = false)
    @Setter
    private String password;

    @OneToMany(mappedBy = "user")
    @Setter
    private List<Task> tasks;

    // Default constructor for JPA
    protected User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}