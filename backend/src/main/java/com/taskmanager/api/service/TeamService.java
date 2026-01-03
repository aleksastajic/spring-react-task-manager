package com.taskmanager.api.service;

import com.taskmanager.api.entity.Team;

import java.util.List;

public interface TeamService {
    Team createTeam(Team team);
    Team addMember(Long teamId, Long userId);
    List<Team> listTeamsForUser(Long userId);
    com.taskmanager.api.entity.User findUserByUsername(String username);

    Team updateTeam(Long teamId, com.taskmanager.api.dto.TeamDto dto, String username);
    void deleteTeam(Long teamId, String username);
    Team removeMember(Long teamId, Long userId, String username);
    List<com.taskmanager.api.entity.User> listMembers(Long teamId);
    com.taskmanager.api.dto.UserDto toUserDto(com.taskmanager.api.entity.User user);
}
