package com.online.store.demo.reactivepostgresspring;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest 
@ActiveProfiles("test")
class ReactivePostgresSpringApplicationTest {

	@Test
	void contextLoads() {
	}

}
