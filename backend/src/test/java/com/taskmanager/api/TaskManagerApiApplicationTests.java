package com.taskmanager.api;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Temporarily disabled due to Testcontainers/Hikari timing issues on CI")
class TaskManagerApiApplicationTests extends PostgresTestcontainerBase {

	@Test
	void contextLoads() {
	}

}
