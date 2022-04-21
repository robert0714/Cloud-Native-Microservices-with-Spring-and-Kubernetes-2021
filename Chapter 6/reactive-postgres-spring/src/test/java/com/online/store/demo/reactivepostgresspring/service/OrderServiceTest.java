package com.online.store.demo.reactivepostgresspring.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.online.store.demo.reactivepostgresspring.controller.OrderControllerTest;
import com.online.store.demo.reactivepostgresspring.model.Order;

import io.netty.handler.codec.http.HttpContentEncoder.Result;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ActiveProfiles("test")
public class OrderServiceTest {

	@Autowired
	private OrderService service;

	Order createPseudo() {
		Order request = new Order();
		request.setP_name("test order");
		return request;
	}

	@BeforeEach
	protected void setUp() throws Exception {
	}

	@Test
	public void testCreateOrder() {
		Mono<Order> result = service.createOrder(createPseudo());
		assertNotNull(result.block().getId());
	}

	@Test
	public void testUpdateOrder() {
		Mono<Order> created = service.createOrder(createPseudo());
		assertNotNull(created.block().getId());
		created.block().setP_name("testUpdateOrder");
		Mono<Order> result = service.updateOrder(created.block().getId(), created.block());
		assertEquals("testUpdateOrder", result.block().getP_name());
	}

	@Test
	public void testGetAllOrders() {
		for (int i = 0; i < 10; ++i) {
			Mono<Order> created = service.createOrder(createPseudo());
			assertNotNull(created.block().getId());
		}
		assertEquals(true, service.getAllOrders().collectList().block().size() > 9);

	}

	@Test
	public void testFindById() {
		for (int i = 0; i < 10; ++i) {
			Mono<Order> created = service.createOrder(createPseudo());
			assertNotNull(created.block().getId());
			Mono<Order> result = service.findById(created.block().getId());
			assertNotNull(result.block().getId());
		}
	}

	@Test
	public void testDeleteOrder() {
		for (int i = 0; i < 10; ++i) {
			Mono<Order> created = service.createOrder(createPseudo());
			assertNotNull(created.block().getId());
			Mono<Order> result = service.deleteOrder(created.block().getId());
			assertNotNull(result.block().getId());
		}
	}

	@Test
	public void testFindOrdersByQuantity() {
		for (int i = 0; i < 10; ++i) {
			Mono<Order> created = service.createOrder(createPseudo());
			assertNotNull(created.block().getId());
			Flux<Order> result = service.findOrdersByQuantity(0);
			assertEquals(true, result.collectList().block().size() > 9);
		}
	}

	@Test
	public void testFetchOrders() {
		final List<Integer> list=new ArrayList<>();
		for (int i = 0; i < 10; ++i) {
			Mono<Order> created = service.createOrder(createPseudo());
			assertNotNull(created.block().getId());
			list.add(created.block().getId());
			
		}
		Flux<Order> result = service.fetchOrders(list);			
		assertEquals(true, result.collectList().block().size() > 9);
	}

}
