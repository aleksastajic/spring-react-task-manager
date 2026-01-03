package com.taskmanager.api.service;

import com.taskmanager.api.entity.Task;

import java.util.List;

public interface TaskService {
    Task createTask(Task task);
    Task findById(Long id);
    List<Task> listTasksByTeam(Long teamId);
    List<Task> listTasksByUser(Long userId);
    Task updateTask(Long id, com.taskmanager.api.dto.TaskDto dto, String username);
    void deleteTask(Long id, String username);
    Task assignUserToTask(Long taskId, Long userId, String username);
    Task unassignUserFromTask(Long taskId, Long userId, String username);
    Task changeTaskStatus(Long taskId, String status, String username);
}
