package org.api.doit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * Entity class representing a User in the database.
 * A user has a unique username, a password, and a list of tasks.
 */
@Entity
@Table(name = "users")
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Automatically generates a UUID as the user ID
    private UUID id;

    @Column(name = "username", unique = true, nullable = false) // Ensures the username is unique and cannot be null
    @Setter
    private String username;

    @Column(name = "password", nullable = false) // Password cannot be null
    @Setter
    private String password;

    @OneToMany(mappedBy = "user") // One user can have multiple tasks, but each task has a reference to a single user
    @Setter
    private List<Task> tasks;

    /**
     * Default constructor required by JPA.
     * It is protected to prevent direct usage outside the persistence context.
     */
    protected User() {}

    /**
     * Constructor for creating a User with a username and password.
     *
     * @param username the unique username for the user
     * @param password the password for the user
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
