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
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
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

        User charlie = new User();
        charlie.setUsername("charlie");
        charlie.setEmail("charlie@example.com");
        charlie.setDisplayName("Charlie C.");
        charlie.setPassword(passwordEncoder.encode("password"));
        charlie.setRoles(new HashSet<>(Set.of(userRole)));
        charlie = userRepo.save(charlie);

        User dana = new User();
        dana.setUsername("dana");
        dana.setEmail("dana@example.com");
        dana.setDisplayName("Dana D.");
        dana.setPassword(passwordEncoder.encode("password"));
        dana.setRoles(new HashSet<>(Set.of(userRole)));
        dana = userRepo.save(dana);

        Team alpha = new Team();
        alpha.setName("Alpha Team");
        alpha.setDescription("Product team focused on core features.");
        alpha.setAdmin(bob);
        alpha.getMembers().add(alice);
        alpha.getMembers().add(bob);
        alpha.getMembers().add(charlie);
        alpha = teamRepo.save(alpha);

        Team beta = new Team();
        beta.setName("Beta Team");
        beta.setDescription("Internal tooling & QA improvements.");
        beta.setAdmin(alice);
        beta.getMembers().add(alice);
        beta.getMembers().add(bob);
        beta.getMembers().add(dana);
        beta = teamRepo.save(beta);

        Team gamma = new Team();
        gamma.setName("Gamma Team");
        gamma.setDescription("Customer success workflows and support ops.");
        gamma.setAdmin(bob);
        gamma.getMembers().add(bob);
        gamma.getMembers().add(charlie);
        gamma = teamRepo.save(gamma);

        // A richer task set so the dashboard has meaningful stats
        LocalDateTime now = LocalDateTime.now();

        Task t1 = new Task();
        t1.setTitle("Design landing page");
        t1.setDescription("Create first draft for marketing landing page.");
        t1.setCreator(alice);
        t1.setTeam(alpha);
        t1.setPriority(Priority.MEDIUM);
        t1.setStatus(Status.TO_DO);
        t1.setDueDate(now.plusDays(7));
        t1.getAssignees().add(alice);

        Task t2 = new Task();
        t2.setTitle("Implement auth API");
        t2.setDescription("Finish JWT login, refresh tokens and tests.");
        t2.setCreator(bob);
        t2.setTeam(alpha);
        t2.setPriority(Priority.HIGH);
        t2.setStatus(Status.IN_PROGRESS);
        t2.setDueDate(now.plusDays(2));
        t2.getAssignees().add(alice);
        t2.getAssignees().add(bob);

        Task t3 = new Task();
        t3.setTitle("Fix flaky CI build");
        t3.setDescription("Investigate intermittent failures and stabilize pipeline.");
        t3.setCreator(bob);
        t3.setTeam(beta);
        t3.setPriority(Priority.HIGH);
        t3.setStatus(Status.BLOCKED);
        t3.setDueDate(now.plusDays(1));
        t3.getAssignees().add(alice);

        Task t4 = new Task();
        t4.setTitle("Add dashboard stats");
        t4.setDescription("Show task breakdown, due soon, and key metrics.");
        t4.setCreator(alice);
        t4.setTeam(beta);
        t4.setPriority(Priority.MEDIUM);
        t4.setStatus(Status.DONE);
        t4.setDueDate(now.minusDays(4));
        t4.getAssignees().add(alice);

        Task t5 = new Task();
        t5.setTitle("Refactor task list UI");
        t5.setDescription("Improve readability and mobile layout.");
        t5.setCreator(alice);
        t5.setTeam(alpha);
        t5.setPriority(Priority.LOW);
        t5.setStatus(Status.TO_DO);
        t5.setDueDate(now.plusDays(10));
        t5.getAssignees().add(charlie);

        Task t6 = new Task();
        t6.setTitle("On-call rotation draft");
        t6.setDescription("Draft weekly rotation and escalation policy.");
        t6.setCreator(bob);
        t6.setTeam(gamma);
        t6.setPriority(Priority.MEDIUM);
        t6.setStatus(Status.IN_PROGRESS);
        t6.setDueDate(now.plusDays(5));
        t6.getAssignees().add(charlie);

        Task t7 = new Task();
        t7.setTitle("Clean up old support tickets");
        t7.setDescription("Archive resolved tickets and update tags.");
        t7.setCreator(charlie);
        t7.setTeam(gamma);
        t7.setPriority(Priority.LOW);
        t7.setStatus(Status.DONE);
        t7.setDueDate(now.minusDays(2));
        t7.getAssignees().add(charlie);

        Task t8 = new Task();
        t8.setTitle("Write user onboarding checklist");
        t8.setDescription("Document onboarding steps for new team members.");
        t8.setCreator(dana);
        t8.setTeam(beta);
        t8.setPriority(Priority.MEDIUM);
        t8.setStatus(Status.TO_DO);
        t8.setDueDate(now.plusDays(3));
        t8.getAssignees().add(dana);

        Task t9 = new Task();
        t9.setTitle("Review sprint backlog");
        t9.setDescription("Triage incoming tasks and set priorities.");
        t9.setCreator(bob);
        t9.setTeam(alpha);
        t9.setPriority(Priority.HIGH);
        t9.setStatus(Status.IN_PROGRESS);
        t9.setDueDate(now.plusDays(3));
        t9.getAssignees().add(bob);

        Task t10 = new Task();
        t10.setTitle("Overdue demo task");
        t10.setDescription("Intentionally overdue so dashboard shows overdue count.");
        t10.setCreator(alice);
        t10.setTeam(alpha);
        t10.setPriority(Priority.MEDIUM);
        t10.setStatus(Status.TO_DO);
        t10.setDueDate(now.minusDays(1));
        t10.getAssignees().add(alice);

        taskRepo.saveAll(List.of(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10));
    }
}
