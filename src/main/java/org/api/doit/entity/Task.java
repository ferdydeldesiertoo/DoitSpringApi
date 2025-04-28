package org.api.doit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity class representing a Task in the database.
 * A task is associated with a user and has a title, optional description,
 * completion status, and timestamps for creation and updates.
 */
@Entity
@Table(name = "tasks")
@Getter
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Automatically generates a UUID as the task ID
    private UUID id;

    @Column(name = "title", nullable = false)
    @Setter
    private String title;

    @Column(name="description", nullable = true)
    @Setter
    private String description;

    @Column(name = "completed", nullable = false)
    @Setter
    private boolean completed = false;

    @ManyToOne(fetch = FetchType.LAZY) // Loads the user only when accessed to improve performance
    @JoinColumn(name = "user_id", nullable = false) // Foreign key linking to the user
    @Setter
    private User user;

    @CreationTimestamp // Automatically sets the creation time when the task is first saved
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp // Automatically updates this timestamp whenever the task is modified
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Default constructor required by JPA.
     */
    public Task() {}

    /**
     * Constructor for creating a Task with title, description, and associated user.
     *
     * @param title the name of the task
     * @param description the optional description of the task
     * @param user the user who owns the task
     */
    public Task(String title, String description, User user) {
        this.title = title;
        this.description = description;
        this.user = user;
    }

    /**
     * Toggles the completion status of the task.
     * If the task is completed, it becomes incomplete, and vice versa.
     */
    public void toggleCompleted() {
        completed = !completed;
    }
}
