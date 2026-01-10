package com.taskmanager.api.config;

import com.taskmanager.api.entity.Role;
import com.taskmanager.api.entity.Team;
import com.taskmanager.api.entity.Task;
import com.taskmanager.api.entity.User;
import com.taskmanager.api.entity.Priority;
import com.taskmanager.api.entity.Status;
import com.taskmanager.api.repository.RoleRepository;
import com.taskmanager.api.repository.TeamRepository;
import com.taskmanager.api.repository.TaskRepository;
import com.taskmanager.api.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Configuration
@Profile("dev")
public class DevDataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final TeamRepository teamRepo;
    private final TaskRepository taskRepo;
    private final PasswordEncoder passwordEncoder;

    public DevDataSeeder(RoleRepository roleRepo, UserRepository userRepo, TeamRepository teamRepo, TaskRepository taskRepo, PasswordEncoder passwordEncoder) {
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
        this.teamRepo = teamRepo;
        this.taskRepo = taskRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepo.count() > 0) return; // avoid reseeding

        Role userRole = roleRepo.findByName("ROLE_USER").orElseGet(() -> roleRepo.save(new Role("ROLE_USER")));
        Role adminRole = roleRepo.findByName("ROLE_ADMIN").orElseGet(() -> roleRepo.save(new Role("ROLE_ADMIN")));

        User alice = new User();
        alice.setUsername("alice");
        alice.setEmail("alice@example.com");
        alice.setDisplayName("Alice A.");
        alice.setPassword(passwordEncoder.encode("password"));
        alice.setRoles(new HashSet<>(Set.of(userRole)));
        alice = userRepo.save(alice);

        User bob = new User();
        bob.setUsername("bob");
        bob.setEmail("bob@example.com");
        bob.setDisplayName("Bob B.");
        bob.setPassword(passwordEncoder.encode("password"));
        Set<Role> bobRoles = new HashSet<>();
        bobRoles.add(userRole);
        bobRoles.add(adminRole);
        bob.setRoles(bobRoles);
        bob = userRepo.save(bob);

        Team alpha = new Team();
        alpha.setName("Alpha Team");
        alpha.setDescription("Product team focused on core features.");
        alpha.setAdmin(bob);
        alpha.getMembers().add(alice);
        alpha.getMembers().add(bob);
        alpha = teamRepo.save(alpha);

        Task t1 = new Task();
        t1.setTitle("Design landing page");
        t1.setDescription("Create first draft for marketing landing page.");
        t1.setCreator(alice);
        t1.setTeam(alpha);
        t1.setPriority(Priority.MEDIUM);
        t1.setStatus(Status.TO_DO);
        t1.setDueDate(LocalDate.now().plusDays(7).atStartOfDay());
        taskRepo.save(t1);

        Task t2 = new Task();
        t2.setTitle("Implement auth API");
        t2.setDescription("Finish JWT login, refresh tokens and tests.");
        t2.setCreator(bob);
        t2.setTeam(alpha);
        t2.setPriority(Priority.HIGH);
        t2.setStatus(Status.IN_PROGRESS);
        t2.setDueDate(LocalDate.now().plusDays(3).atStartOfDay());
        t2.getAssignees().add(alice);
        taskRepo.save(t2);
    }
}
