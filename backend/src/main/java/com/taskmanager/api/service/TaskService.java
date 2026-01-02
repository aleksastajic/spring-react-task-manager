package com.taskmanager.api.service;

import com.taskmanager.api.entity.Task;

import java.util.List;

public interface TaskService {
    Task createTask(Task task);
    Task findById(Long id);
    List<Task> listTasksByTeam(Long teamId);
}
