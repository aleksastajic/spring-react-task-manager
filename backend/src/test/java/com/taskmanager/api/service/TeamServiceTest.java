package com.taskmanager.api.service;

import com.taskmanager.api.entity.Team;
import com.taskmanager.api.repository.TeamRepository;
import com.taskmanager.api.repository.UserRepository;
import com.taskmanager.api.service.impl.TeamServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class TeamServiceTest {
    @Test
    void createTeamSavesTeam() {
        TeamRepository teamRepository = Mockito.mock(TeamRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        Mockito.when(teamRepository.save(any(Team.class))).thenAnswer(inv -> inv.getArgument(0));
        TeamServiceImpl teamService = new TeamServiceImpl(teamRepository, userRepository);
        Team team = new Team();
        team.setName("team1");
        Team saved = teamService.createTeam(team);
        assertEquals("team1", saved.getName());
    }
}
