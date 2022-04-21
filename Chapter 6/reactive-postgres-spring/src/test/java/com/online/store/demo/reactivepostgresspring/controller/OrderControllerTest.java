package com.online.store.demo.reactivepostgresspring.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList; 
import java.util.List; 

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationContext; 
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient; 

import com.online.store.demo.reactivepostgresspring.model.Order;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ActiveProfiles("test")
public class OrderControllerTest {
	private WebTestClient client;
	final String uri = "/orders";
	
	@Autowired
	private ApplicationContext applicationContext ;

	@BeforeEach
	void setup() throws Exception {
		this.client = WebTestClient.bindToApplicationContext(applicationContext).build();
	}

	@BeforeEach
	protected void setUp() throws Exception {
	}

	@Test
	public void testCreateOrder()  {
		Order request = new Order();
		request.setP_name("test order");
		client
		   .post()
		   .uri(uri)
		   .contentType(MediaType.APPLICATION_JSON)
		   .body(Mono.just(request), Order.class)
		   .exchange()
				.expectStatus().is2xxSuccessful();

		EntityExchangeResult<Order> response = client
		   .post()
		   .uri(uri)
		   .contentType(MediaType.APPLICATION_JSON)
		   .body(Mono.just(request), Order.class)
		   .exchange().expectStatus().is2xxSuccessful()
				.expectBody(Order.class).returnResult();
		
		Order order =  response.getResponseBody();

		assertNotNull(order);
		final Integer orderUuid = order.getId();
		assertNotNull(orderUuid);
		log.info("--------------------------------------------------------");
		log.info(orderUuid.toString());
		assertEquals("test order", order.getP_name());		 
	}

	@Test
	public void testUpdateOrder() {
		Order request = new Order();
		request.setP_name("test order");
		for(int i=0 ; i<10;++i ) {
			client
			   .post()
			   .uri(uri)
			   .contentType(MediaType.APPLICATION_JSON)
			   .body(Mono.just(request), Order.class)
			   .exchange()
					.expectStatus().is2xxSuccessful();
		}
		List<String> list =new ArrayList<>();
		 client.get()
		   .uri(uri)
		   .exchange().expectStatus().is2xxSuccessful()
		   .expectBody()
			.jsonPath("*.id").isNotEmpty()
			.jsonPath("*.id").value(result -> {
				System.out.println(result);
				System.out.println(result.getClass().getCanonicalName());
				final net.minidev.json.JSONArray array = (net.minidev.json.JSONArray) result;
				array.forEach(						
					unit -> {
							list.add(unit.toString());			
					
				});
			});
		
		 for (String string : list) {
			 request.setId(Integer.valueOf(string));
			 client
					   .put()
					   .uri("/orders/"+string)
					   .contentType(MediaType.APPLICATION_JSON)
					   .body(Mono.just(request), Order.class)
					   .exchange().expectStatus()
					   .is2xxSuccessful()
						.expectBody().returnResult();
		};
	}

	@Test
	public void testDeleteOrder() {
		Order request = new Order();
		request.setP_name("test order");
		for(int i=0 ; i<10;++i ) {
			client
			   .post()
			   .uri(uri)
			   .contentType(MediaType.APPLICATION_JSON)
			   .body(Mono.just(request), Order.class)
			   .exchange()
					.expectStatus().is2xxSuccessful();
		}
		List<String> list =new ArrayList<>();
		 client.get()
		   .uri(uri)
		   .exchange().expectStatus().is2xxSuccessful()
		   .expectBody()
			.jsonPath("*.id").isNotEmpty()
			.jsonPath("*.id").value(result -> {
				System.out.println(result);
				System.out.println(result.getClass().getCanonicalName());
				final net.minidev.json.JSONArray array = (net.minidev.json.JSONArray) result;
				array.forEach(						
					unit -> {
							list.add(unit.toString());			
					
				});
			});
		 for (String string : list) {
			 request.setId(Integer.valueOf(string));
			 client.delete()
					.uri(uri+"/"+string)  
					   .exchange().expectStatus()
					   .is2xxSuccessful()
					   .expectBody();
		};
	}

	@Test
	public void testGetAllOrders() {		
		Order request = new Order();
		request.setP_name("test order");
		for(int i=0 ; i<10;++i ) {
			client
			   .post()
			   .uri(uri)
			   .contentType(MediaType.APPLICATION_JSON)
			   .body(Mono.just(request), Order.class)
			   .exchange()
					.expectStatus().is2xxSuccessful();
		}
		 client.get()
		   .uri(uri)
		   .exchange().expectStatus().is2xxSuccessful()
		   .expectBody()
			.jsonPath("*.id").isNotEmpty()
			.jsonPath("*.id").value(result -> {
				System.out.println(result);
				System.out.println(result.getClass().getCanonicalName());
				final net.minidev.json.JSONArray array = (net.minidev.json.JSONArray) result;
				array.forEach(unit -> {

				});
			});
	}

	@Test
	public void testGetOrderById() {
		Order request = new Order();
		request.setP_name("test order");
		for(int i=0 ; i<10;++i ) {
			client
			   .post()
			   .uri(uri)
			   .contentType(MediaType.APPLICATION_JSON)
			   .body(Mono.just(request), Order.class)
			   .exchange()
					.expectStatus().is2xxSuccessful();
		}
		List<String> list =new ArrayList<>();
		 client.get()
		   .uri(uri)
		   .exchange().expectStatus().is2xxSuccessful()
		   .expectBody()
			.jsonPath("*.id").isNotEmpty()
			.jsonPath("*.id").value(result -> {
				System.out.println(result);
				System.out.println(result.getClass().getCanonicalName());
				final net.minidev.json.JSONArray array = (net.minidev.json.JSONArray) result;
				array.forEach(						
					unit -> {
							list.add(unit.toString());			
					
				});
			});
		
		 for (String string : list) {
			 request.setId(Integer.valueOf(string));
			 client.get()
					.uri("/orders/"+string)  
					   .exchange().expectStatus()
					   .is2xxSuccessful()
					   .expectBody()
						.jsonPath("$.id").isNotEmpty()
						.jsonPath("$.id").isEqualTo(string);
		};
	}

	@Test
	public void testGetUsersByQuantity() {
		Order request = new Order();
		request.setP_name("test order");
		client
		   .post()
		   .uri(uri)
		   .contentType(MediaType.APPLICATION_JSON)
		   .body(Mono.just(request), Order.class)
		   .exchange()
				.expectStatus().is2xxSuccessful();
		client.get()
		   .uri("/orders/qty/0")
		   .exchange().expectStatus().is2xxSuccessful()
		   .expectBody()
		   .jsonPath("*.id").isNotEmpty()
			.jsonPath("*.id").value(result -> {
				System.out.println(result);
				System.out.println(result.getClass().getCanonicalName());
				 
			});
	}

	@Test
	public void testFetchUsersByIds() {
		Order request = new Order();
		request.setP_name("test order");
		for(int i=0 ; i<10;++i ) {
			client
			   .post()
			   .uri(uri)
			   .contentType(MediaType.APPLICATION_JSON)
			   .body(Mono.just(request), Order.class)
			   .exchange()
					.expectStatus().is2xxSuccessful();
		}
		List<String> list =new ArrayList<>();
		 client.get()
		   .uri(uri)
		   .exchange().expectStatus().is2xxSuccessful()
		   .expectBody()
			.jsonPath("*.id").isNotEmpty()
			.jsonPath("*.id").value(result -> {
				System.out.println(result);
				System.out.println(result.getClass().getCanonicalName());
				final net.minidev.json.JSONArray array = (net.minidev.json.JSONArray) result;
				array.forEach(						
					unit -> {
							list.add(unit.toString());			
					
				});
			});
		 client.post()
		   .uri(uri+"/search/id")
		   .body(Mono.just(list), List.class)
		   .exchange().expectStatus().is2xxSuccessful()
		   .expectBody()
			.jsonPath("*.id").isNotEmpty()
			.jsonPath("*.id").value(result -> {
				System.out.println(result);
				System.out.println(result.getClass().getCanonicalName());
				 
			});
	}

}
