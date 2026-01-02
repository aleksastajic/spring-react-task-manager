package com.taskmanager.api.service.impl;

import com.taskmanager.api.entity.Team;
import com.taskmanager.api.entity.User;
import com.taskmanager.api.repository.TeamRepository;
import com.taskmanager.api.repository.UserRepository;
import com.taskmanager.api.service.TeamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public TeamServiceImpl(TeamRepository teamRepository, UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Team createTeam(Team team) {
        return teamRepository.save(team);
    }

    @Override
    @Transactional
    public Team addMember(Long teamId, Long userId) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new RuntimeException("Team not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        team.getMembers().add(user);
        return teamRepository.save(team);
    }

    @Override
    public List<Team> listTeamsForUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return teamRepository.findAll().stream().filter(t -> t.getMembers().contains(user)).toList();
    }
}
