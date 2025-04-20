package org.api.doit.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.api.doit.dto.CreateTaskRequest;
import org.api.doit.dto.TaskResponse;
import org.api.doit.entity.Task;
import org.api.doit.entity.User;
import org.api.doit.repository.TaskRepository;
import org.api.doit.security.AuthenticationFacade;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final AuthenticationFacade authenticationFacade;
    private final EntityManager entityManager;

    public TaskService(final TaskRepository taskRepository,
                       final UserService userService,
                       final AuthenticationFacade authenticationFacade,
                       final EntityManager entityManager) {
        this.userService = userService;
        this.authenticationFacade = authenticationFacade;
        this.entityManager = entityManager;
        this.taskRepository = taskRepository;
    }

    @Transactional
    public TaskResponse createTask(final CreateTaskRequest createTaskRequest) {
        User userRef = entityManager.getReference(User.class, authenticationFacade.getId());

        Task task = new Task(createTaskRequest.title(), createTaskRequest.description(), userRef);

        taskRepository.save(task);

        return new TaskResponse(task.getId(), task.getTitle(), task.getDescription(), task.isCompleted());
    }

    @Transactional
    public List<TaskResponse> getTasks() {
        List<Task> tasks = taskRepository.findByUserIdOrderByCreatedAt(authenticationFacade.getId());
        return tasks.stream()
                    .map((task) ->
                            new TaskResponse(
                                    task.getId(),
                                    task.getTitle(),
                                    task.getDescription(),
                                    task.isCompleted()))
                    .toList();
    }

    @Transactional
    public TaskResponse markTaskAsCompleted(final UUID id) {
        Task task = taskRepository.findByIdAndUserId(id, authenticationFacade.getId())
                .orElseThrow(() -> new EntityNotFoundException("Not found"));

        task.toggleCompleted();

        return new TaskResponse(task.getId(), task.getTitle(), task.getDescription(), task.isCompleted());
    }
}
