package com.taskmanager.api.service.impl;

import com.taskmanager.api.entity.Task;
import com.taskmanager.api.entity.Team;
import com.taskmanager.api.repository.TaskRepository;
import com.taskmanager.api.repository.TeamRepository;
import com.taskmanager.api.service.TaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TeamRepository teamRepository;

    public TaskServiceImpl(TaskRepository taskRepository, TeamRepository teamRepository) {
        this.taskRepository = taskRepository;
        this.teamRepository = teamRepository;
    }

    @Override
    @Transactional
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public Task findById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
    }

    @Override
    public List<Task> listTasksByTeam(Long teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new RuntimeException("Team not found"));
        return taskRepository.findAll().stream().filter(t -> t.getTeam() != null && t.getTeam().getId().equals(team.getId())).toList();
    }
}
