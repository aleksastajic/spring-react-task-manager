package com.taskmanager.api.service;

import com.taskmanager.api.entity.Task;
import com.taskmanager.api.entity.Team;
import com.taskmanager.api.entity.User;
import com.taskmanager.api.repository.TaskRepository;
import com.taskmanager.api.repository.TeamRepository;
import com.taskmanager.api.service.impl.TaskServiceImpl;
import com.taskmanager.api.repository.UserRepository;
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
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        TaskServiceImpl taskService = new TaskServiceImpl(taskRepository, teamRepository, userRepository);
        Task task = new Task();
        task.setTitle("task1");
        Task saved = taskService.createTask(task);
        assertEquals("task1", saved.getTitle());
    }

    @Test
    void updateTaskByCreator() {
        TaskRepository taskRepository = Mockito.mock(TaskRepository.class);
        TeamRepository teamRepository = Mockito.mock(TeamRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        Mockito.when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskServiceImpl taskService = new TaskServiceImpl(taskRepository, teamRepository, userRepository);

        Task task = new Task();
        task.setId(1L);
        User creator = new User();
        creator.setId(2L);
        creator.setUsername("creator");
        task.setCreator(creator);
        task.setTitle("old");

        Mockito.when(taskRepository.findById(1L)).thenReturn(java.util.Optional.of(task));
        Mockito.when(userRepository.findByUsername("creator")).thenReturn(java.util.Optional.of(creator));

        com.taskmanager.api.dto.TaskDto dto = new com.taskmanager.api.dto.TaskDto();
        dto.setTitle("new title");

        Task updated = taskService.updateTask(1L, dto, "creator");
        assertEquals("new title", updated.getTitle());
    }

    @Test
    void assignUserToTaskByAdmin() {
        TaskRepository taskRepository = Mockito.mock(TaskRepository.class);
        TeamRepository teamRepository = Mockito.mock(TeamRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        Mockito.when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskServiceImpl taskService = new TaskServiceImpl(taskRepository, teamRepository, userRepository);

        Task task = new Task();
        task.setId(10L);
        Team team = new Team();
        User admin = new User();
        admin.setId(5L);
        admin.setUsername("admin");
        team.setAdmin(admin);
        task.setTeam(team);

        User userToAssign = new User();
        userToAssign.setId(7L);

        Mockito.when(taskRepository.findById(10L)).thenReturn(java.util.Optional.of(task));
        Mockito.when(userRepository.findByUsername("admin")).thenReturn(java.util.Optional.of(admin));
        Mockito.when(userRepository.findById(7L)).thenReturn(java.util.Optional.of(userToAssign));

        Task updated = taskService.assignUserToTask(10L, 7L, "admin");
        assertTrue(updated.getAssignees().contains(userToAssign));
    }

    @Test
    void unassignUserSelf() {
        TaskRepository taskRepository = Mockito.mock(TaskRepository.class);
        TeamRepository teamRepository = Mockito.mock(TeamRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        Mockito.when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskServiceImpl taskService = new TaskServiceImpl(taskRepository, teamRepository, userRepository);

        Task task = new Task();
        task.setId(20L);
        User user = new User();
        user.setId(8L);
        user.setUsername("self");
        task.getAssignees().add(user);

        Mockito.when(taskRepository.findById(20L)).thenReturn(java.util.Optional.of(task));
        Mockito.when(userRepository.findByUsername("self")).thenReturn(java.util.Optional.of(user));
        Mockito.when(userRepository.findById(8L)).thenReturn(java.util.Optional.of(user));

        Task updated = taskService.unassignUserFromTask(20L, 8L, "self");
        assertFalse(updated.getAssignees().contains(user));
    }

    @Test
    void changeStatusByAssignee() {
        TaskRepository taskRepository = Mockito.mock(TaskRepository.class);
        TeamRepository teamRepository = Mockito.mock(TeamRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        Mockito.when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskServiceImpl taskService = new TaskServiceImpl(taskRepository, teamRepository, userRepository);

        Task task = new Task();
        task.setId(30L);
        User user = new User();
        user.setId(9L);
        user.setUsername("worker");
        task.getAssignees().add(user);

        Mockito.when(taskRepository.findById(30L)).thenReturn(java.util.Optional.of(task));
        Mockito.when(userRepository.findByUsername("worker")).thenReturn(java.util.Optional.of(user));

        Task updated = taskService.changeTaskStatus(30L, "IN_PROGRESS", "worker");
        assertEquals(com.taskmanager.api.entity.Status.IN_PROGRESS, updated.getStatus());
    }
}
