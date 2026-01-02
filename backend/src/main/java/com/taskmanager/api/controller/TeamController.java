package com.taskmanager.api.controller;

import com.taskmanager.api.dto.TeamDto;
import com.taskmanager.api.entity.Team;
import com.taskmanager.api.service.TeamService;
import com.taskmanager.api.mapper.TeamMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for team management endpoints.
 */
@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;
    private final TeamMapper teamMapper;

    public TeamController(TeamService teamService, TeamMapper teamMapper) {
        this.teamService = teamService;
        this.teamMapper = teamMapper;
    }

    /**
     * Create a new team from the provided payload.
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TeamDto> createTeam(@Valid @RequestBody TeamDto dto) {
        Team team = new Team();
        team.setName(dto.getName());
        team.setDescription(dto.getDescription());
        Team created = teamService.createTeam(team);
        TeamDto out = teamMapper.toDto(created);
        return ResponseEntity.ok(out);
    }

    @PostMapping("/{teamId}/members/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TeamDto> addMember(@PathVariable Long teamId, @PathVariable Long userId) {
        Team updated = teamService.addMember(teamId, userId);
        return ResponseEntity.ok(teamMapper.toDto(updated));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TeamDto>> listTeamsForUser(@RequestParam Long userId) {
        List<Team> teams = teamService.listTeamsForUser(userId);
        List<TeamDto> dtos = teams.stream().map(teamMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
}
