package com.taskmanager.api.controller;


import com.taskmanager.api.dto.TaskCreateDto;
import com.taskmanager.api.dto.TaskDto;
import com.taskmanager.api.entity.Task;
import com.taskmanager.api.entity.User;
import com.taskmanager.api.service.TaskService;
import com.taskmanager.api.repository.UserRepository;
import com.taskmanager.api.mapper.TaskMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller exposing task management endpoints.
 * Supports creation, retrieval and listing of tasks by team.
 */
@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Endpoints for task management.")
public class TaskController {

    private final TaskService taskService;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    public TaskController(TaskService taskService, UserRepository userRepository, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.userRepository = userRepository;
        this.taskMapper = taskMapper;
    }

    /**
     * Create a new task from the provided `TaskCreateDto`.
     */
    @Operation(summary = "Create task", description = "Create a new task from the provided TaskCreateDto.")
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskCreateDto createDto) {
        Task task = new Task();
        task.setTitle(createDto.getTitle());
        task.setDescription(createDto.getDescription());
        task.setDueDate(createDto.getDueDate());
        task.setPriority(createDto.getPriority() != null ? com.taskmanager.api.entity.Priority.valueOf(createDto.getPriority()) : null);
        if (createDto.getTeamId() != null) {
            com.taskmanager.api.entity.Team t = new com.taskmanager.api.entity.Team();
            t.setId(createDto.getTeamId());
            task.setTeam(t);
        }
        if (createDto.getAssigneeIds() != null) {
            for (Long id : createDto.getAssigneeIds()) {
                User u = userRepository.findById(id).orElse(null);
                if (u != null) task.getAssignees().add(u);
            }
        }
        Task created = taskService.createTask(task);
        return ResponseEntity.ok(taskMapper.toDto(created));
    }

    /**
     * Retrieve a task by its id.
     */
    @Operation(summary = "Get task by ID", description = "Retrieve a task by its ID.")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskDto> getTask(@PathVariable Long id) {
        Task t = taskService.findById(id);
        return ResponseEntity.ok(taskMapper.toDto(t));
    }

    /**
     * List tasks for a given team id.
     */
    @Operation(summary = "List tasks by team", description = "List all tasks for a given team ID.")
    @GetMapping("/team/{teamId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TaskDto>> listByTeam(@PathVariable Long teamId) {
        List<Task> tasks = taskService.listTasksByTeam(teamId);
        List<TaskDto> dtos = tasks.stream().map(taskMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
}
