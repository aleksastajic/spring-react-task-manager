package com.taskmanager.api.service;

import com.taskmanager.api.entity.Team;
import com.taskmanager.api.entity.User;
import com.taskmanager.api.repository.TeamRepository;
import com.taskmanager.api.repository.UserRepository;
import com.taskmanager.api.service.impl.TeamServiceImpl;
import com.taskmanager.api.mapper.TeamMapper;
import com.taskmanager.api.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class TeamServiceTest {
    @Test
    void createTeamSavesTeam() {
        TeamRepository teamRepository = Mockito.mock(TeamRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserMapper userMapper = Mockito.mock(UserMapper.class);
        TeamMapper teamMapper = new TeamMapper(userMapper);
        Mockito.when(teamRepository.save(any(Team.class))).thenAnswer(inv -> inv.getArgument(0));
        TeamServiceImpl teamService = new TeamServiceImpl(teamRepository, userRepository, teamMapper, userMapper);
        Team team = new Team();
        team.setName("team1");
        Team saved = teamService.createTeam(team);
        assertEquals("team1", saved.getName());
    }

    @Test
    void addMemberByAdmin() {
        TeamRepository teamRepository = Mockito.mock(TeamRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserMapper userMapper = Mockito.mock(UserMapper.class);
        TeamMapper teamMapper = new TeamMapper(userMapper);
        Mockito.when(teamRepository.save(any(Team.class))).thenAnswer(inv -> inv.getArgument(0));

        TeamServiceImpl teamService = new TeamServiceImpl(teamRepository, userRepository, teamMapper, userMapper);

        Team team = new Team();
        team.setId(1L);
        User admin = new User();
        admin.setId(2L);
        admin.setUsername("admin");
        team.setAdmin(admin);

        User userToAdd = new User();
        userToAdd.setId(3L);

        Mockito.when(teamRepository.findById(1L)).thenReturn(java.util.Optional.of(team));
        Mockito.when(userRepository.findByUsername("admin")).thenReturn(java.util.Optional.of(admin));
        Mockito.when(userRepository.findById(3L)).thenReturn(java.util.Optional.of(userToAdd));

        Team updated = teamService.addMember(1L, 3L, "admin");
        assertTrue(updated.getMembers().contains(userToAdd));
    }

    @Test
    void removeMemberBySelfOrAdmin() {
        TeamRepository teamRepository = Mockito.mock(TeamRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserMapper userMapper = Mockito.mock(UserMapper.class);
        TeamMapper teamMapper = new TeamMapper(userMapper);
        Mockito.when(teamRepository.save(any(Team.class))).thenAnswer(inv -> inv.getArgument(0));

        TeamServiceImpl teamService = new TeamServiceImpl(teamRepository, userRepository, teamMapper, userMapper);

        Team team = new Team();
        team.setId(5L);
        User admin = new User(); admin.setId(10L); admin.setUsername("adm");
        User member = new User(); member.setId(11L); member.setUsername("mem");
        team.setAdmin(admin);
        team.getMembers().add(member);

        Mockito.when(teamRepository.findById(5L)).thenReturn(java.util.Optional.of(team));
        Mockito.when(userRepository.findByUsername("mem")).thenReturn(java.util.Optional.of(member));
        Mockito.when(userRepository.findById(11L)).thenReturn(java.util.Optional.of(member));

        Team after = teamService.removeMember(5L, 11L, "mem");
        assertFalse(after.getMembers().contains(member));
    }

    @Test
    void updateAndDeleteByAdmin() {
        TeamRepository teamRepository = Mockito.mock(TeamRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserMapper userMapper = Mockito.mock(UserMapper.class);
        TeamMapper teamMapper = new TeamMapper(userMapper);
        Mockito.when(teamRepository.save(any(Team.class))).thenAnswer(inv -> inv.getArgument(0));

        TeamServiceImpl teamService = new TeamServiceImpl(teamRepository, userRepository, teamMapper, userMapper);

        Team team = new Team(); team.setId(8L); team.setName("oldName");
        User admin = new User(); admin.setId(9L); admin.setUsername("boss");
        team.setAdmin(admin);

        Mockito.when(teamRepository.findById(8L)).thenReturn(java.util.Optional.of(team));
        Mockito.when(userRepository.findByUsername("boss")).thenReturn(java.util.Optional.of(admin));

        com.taskmanager.api.dto.TeamDto dto = new com.taskmanager.api.dto.TeamDto();
        dto.setName("newName");
        Team updated = teamService.updateTeam(8L, dto, "boss");
        assertEquals("newName", updated.getName());

        // delete
        Mockito.when(teamRepository.findById(8L)).thenReturn(java.util.Optional.of(team));
        teamService.deleteTeam(8L, "boss");
        // if no exception thrown, assume success
    }

    @Test
    void listTeamsForUserReturnsMatching() {
        TeamRepository teamRepository = Mockito.mock(TeamRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserMapper userMapper = Mockito.mock(UserMapper.class);
        TeamMapper teamMapper = new TeamMapper(userMapper);

        Team t1 = new Team(); t1.setId(1L);
        Team t2 = new Team(); t2.setId(2L);

        User user = new User(); user.setId(20L);
        t1.getMembers().add(user);

        Mockito.when(teamRepository.findAll()).thenReturn(java.util.List.of(t1, t2));
        Mockito.when(userRepository.findById(20L)).thenReturn(java.util.Optional.of(user));

        TeamServiceImpl teamService = new TeamServiceImpl(teamRepository, userRepository, teamMapper, userMapper);
        java.util.List<Team> res = teamService.listTeamsForUser(20L);
        assertEquals(1, res.size());
        assertEquals(1L, res.get(0).getId());
    }
}
