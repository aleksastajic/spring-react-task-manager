package com.taskmanager.api.service;

import com.taskmanager.api.entity.Task;
import com.taskmanager.api.repository.TaskRepository;
import com.taskmanager.api.repository.TeamRepository;
import com.taskmanager.api.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class TaskServiceTest {
    @Test
    void createTaskSavesTask() {
        TaskRepository taskRepository = Mockito.mock(TaskRepository.class);
        TeamRepository teamRepository = Mockito.mock(TeamRepository.class);
        Mockito.when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));
        TaskServiceImpl taskService = new TaskServiceImpl(taskRepository, teamRepository);
        Task task = new Task();
        task.setTitle("task1");
        Task saved = taskService.createTask(task);
        assertEquals("task1", saved.getTitle());
    }
}
