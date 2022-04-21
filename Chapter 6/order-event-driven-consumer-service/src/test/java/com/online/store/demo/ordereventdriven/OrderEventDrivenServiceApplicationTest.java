package com.online.store.demo.ordereventdriven;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
public class OrderEventDrivenServiceApplicationTest {

	@BeforeEach
	protected void setUp() throws Exception {
	}

	@Test
	void contextLoads() {
	}

}
