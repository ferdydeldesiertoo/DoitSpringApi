package org.api.doit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity class representing a Task in the bd.
 * A task is associated with a user and has a name, completion status, and timestamps for creation and updates.
 */
@Entity
@Table(name = "tasks")
@Getter
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Setter
    private User user;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Default constructor for JPA
    public Task() {}

    /**
     * Constructor for creating a Task with a name and associated user.
     *
     * @param title the name of the task
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