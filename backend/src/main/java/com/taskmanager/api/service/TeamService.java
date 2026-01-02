package com.taskmanager.api.service;

import com.taskmanager.api.entity.Team;

import java.util.List;

public interface TeamService {
    Team createTeam(Team team);
    Team addMember(Long teamId, Long userId);
    List<Team> listTeamsForUser(Long userId);
}
