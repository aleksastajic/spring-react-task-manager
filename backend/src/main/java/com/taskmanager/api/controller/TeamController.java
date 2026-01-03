
package com.taskmanager.api.controller;


import com.taskmanager.api.dto.TeamDto;
import com.taskmanager.api.entity.Team;
import com.taskmanager.api.service.TeamService;
import com.taskmanager.api.mapper.TeamMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for team management endpoints.
 */
@RestController
@RequestMapping("/api/teams")
@Tag(name = "Teams", description = "Endpoints for team management.")
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
    @Operation(summary = "Create team", description = "Create a new team from the provided payload.")
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TeamDto> createTeam(@Valid @RequestBody TeamDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        String username = auth.getName();
        com.taskmanager.api.entity.User creator = teamService.findUserByUsername(username);
        Team team = new Team();
        team.setName(dto.getName());
        team.setDescription(dto.getDescription());
        team.setAdmin(creator);
        team.getMembers().add(creator);
        Team created = teamService.createTeam(team);
        TeamDto out = teamMapper.toDto(created);
        return ResponseEntity.ok(out);
    }

    @Operation(summary = "Add member to team", description = "Add a user as a member to a team. Requires ADMIN role.")
    @PostMapping("/{teamId}/members/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TeamDto> addMember(@PathVariable Long teamId, @PathVariable Long userId) {
        Team updated = teamService.addMember(teamId, userId);
        return ResponseEntity.ok(teamMapper.toDto(updated));
    }

    @Operation(summary = "List teams for user", description = "List all teams for a given user ID.")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TeamDto>> listTeamsForUser(@RequestParam Long userId) {
        List<Team> teams = teamService.listTeamsForUser(userId);
        List<TeamDto> dtos = teams.stream().map(teamMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    @Operation(summary = "Update team info", description = "Update team name/description. Only admin can update.")
    @PatchMapping("/{teamId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TeamDto> updateTeam(@PathVariable Long teamId, @RequestBody TeamDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Team updated = teamService.updateTeam(teamId, dto, username);
        return ResponseEntity.ok(teamMapper.toDto(updated));
    }

    @Operation(summary = "Delete team", description = "Delete a team. Only admin can delete.")
    @DeleteMapping("/{teamId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long teamId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        teamService.deleteTeam(teamId, username);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Remove member from team", description = "Remove a user from a team. Admin or self can remove.")
    @DeleteMapping("/{teamId}/members/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TeamDto> removeMember(@PathVariable Long teamId, @PathVariable Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Team updated = teamService.removeMember(teamId, userId, username);
        return ResponseEntity.ok(teamMapper.toDto(updated));
    }

    @Operation(summary = "List team members", description = "List all users in a team.")
    @GetMapping("/{teamId}/members")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<com.taskmanager.api.dto.UserDto>> listMembers(@PathVariable Long teamId) {
        List<com.taskmanager.api.entity.User> members = teamService.listMembers(teamId);
        List<com.taskmanager.api.dto.UserDto> dtos = members.stream().map(teamService::toUserDto).toList();
        return ResponseEntity.ok(dtos);
    }
}
