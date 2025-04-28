package org.api.doit.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.api.doit.dto.CreateTaskRequest;
import org.api.doit.dto.TaskResponse;
import org.api.doit.entity.Task;
import org.api.doit.entity.User;
import org.api.doit.exception.TaskNotFoundException;
import org.api.doit.repository.TaskRepository;
import org.api.doit.security.AuthenticationFacade;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service responsible for task-related operations such as creation,
 * retrieval, toggling completion status, and deletion.
 */
@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final AuthenticationFacade authenticationFacade;
    private final EntityManager entityManager;

    /**
     * Constructor for dependency injection.
     *
     * @param taskRepository Task repository interface.
     * @param authenticationFacade Abstraction to retrieve the authenticated user's ID.
     * @param entityManager EntityManager to obtain references to managed entities.
     */
    public TaskService(final TaskRepository taskRepository,
                       final AuthenticationFacade authenticationFacade,
                       final EntityManager entityManager) {
        this.authenticationFacade = authenticationFacade;
        this.entityManager = entityManager;
        this.taskRepository = taskRepository;
    }

    /**
     * Creates a new task for the authenticated user.
     *
     * @param createTaskRequest Data required to create a new task.
     * @return TaskResponse containing task details.
     */
    @Transactional
    public TaskResponse createTask(final CreateTaskRequest createTaskRequest) {
        // Create a reference to the authenticated user without querying the DB
        User userRef = entityManager.getReference(User.class, authenticationFacade.getId());

        Task task = new Task(createTaskRequest.title(), createTaskRequest.description(), userRef);
        entityManager.persist(task);

        entityManager.flush(); //Insert immediately to bd

        return new TaskResponse(task.getId(), task.getTitle(), task.getDescription(), task.isCompleted(), task.getCreatedAt());
    }

    /**
     * Retrieves all tasks belonging to the authenticated user, ordered by creation date.
     *
     * @return List of TaskResponse objects.
     */
    @Transactional
    public List<TaskResponse> getAllTasks() {
        List<Task> tasks = taskRepository.findByUserIdOrderByCreatedAt(authenticationFacade.getId());
        return tasks.stream()
                .map(TaskResponse::from)
                .toList();
    }

    /**
     * Retrieves tasks filtered by their completion status for the current user.
     *
     * @param completed true to get completed tasks, false for incomplete.
     * @return List of TaskResponse objects.
     */
    @Transactional
    public List<TaskResponse> getTasksByCompleted(Boolean completed) {
        List<Task> tasks = taskRepository.findByUserIdAndCompletedOrderByCreatedAt(authenticationFacade.getId(), completed);
        return tasks.stream()
                .map(TaskResponse::from)
                .toList();
    }

    /**
     * Retrieves a specific task by ID, validating that it belongs to the current user.
     *
     * @param id UUID of the task.
     * @return TaskResponse containing task details.
     */
    @Transactional
    public TaskResponse getTask(final UUID id) {
        Task task = taskRepository.findByIdAndUserId(id, authenticationFacade.getId())
                .orElseThrow(() -> new TaskNotFoundException("Task with id " + id + " was not found for the current user."));

        return new TaskResponse(task.getId(), task.getTitle(), task.getDescription(), task.isCompleted(), task.getCreatedAt());
    }

    /**
     * Toggles the completion status of a specific task.
     *
     * @param id UUID of the task.
     * @return TaskResponse reflecting the updated task.
     */
    @Transactional
    public TaskResponse toggleTaskCompleted(final UUID id) {
        Task task = taskRepository.findByIdAndUserId(id, authenticationFacade.getId())
                .orElseThrow(() -> new TaskNotFoundException("Task with id " + id + " was not found for the current user."));

        // Flip the task's completed status
        task.toggleCompleted();

        return new TaskResponse(task.getId(), task.getTitle(), task.getDescription(), task.isCompleted(), task.getCreatedAt());
    }

    /**
     * Deletes a task if it belongs to the current user.
     *
     * @param id UUID of the task to delete.
     */
    @Transactional
    public void deleteTask(final UUID id) {
        Task task = taskRepository.findByIdAndUserId(id, authenticationFacade.getId())
                .orElseThrow(() -> new TaskNotFoundException("Task with id " + id + " was not found for the current user."));

        taskRepository.delete(task);
    }
}
