package org.api.doit.repository;

import org.api.doit.entity.Task;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for accessing Task entities in the database.
 * Extends CrudRepository to provide basic CRUD operations.
 */
public interface TaskRepository extends CrudRepository<Task, Long> {

    /**
     * Retrieves all tasks for a specific user ordered by their creation date.
     *
     * @param userId the ID of the user whose tasks are to be retrieved
     * @return a list of tasks for the specified user
     */
    List<Task> findByUserIdOrderByCreatedAt(UUID userId);

    List<Task> findByUserIdAndCompletedOrderByCreatedAt(UUID userId, boolean completed);

    /**
     * Retrieves a task by its ID and the user ID.
     *
     * @param taskId the ID of the task to retrieve
     * @param userId the ID of the user to whom the task belongs
     * @return an Optional containing the task if found, or empty if not
     */
    Optional<Task> findByIdAndUserId(UUID taskId, UUID userId);
}
