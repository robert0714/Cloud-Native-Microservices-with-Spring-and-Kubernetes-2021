package com.online.store.demo.ordereventdriven.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
 
import com.fasterxml.jackson.databind.ObjectMapper;
import com.online.store.demo.ordereventdriven.model.PurchaseOrder;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT )
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
public class OrderControllerTest {
	@Autowired
	protected TestRestTemplate restTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka ;
	@Autowired
	private EmbeddedKafkaBroker embeddedKafkaBroker;

	@Value("${spring.cloud.stream.bindings.event-producer.destination}")
	private String inputTopic;

//	@Value("${spring.cloud.stream.bindings.event-consumer.destination}")
//	private String outputTopic;
	
	@BeforeEach
	protected void setUp() throws Exception {
		embeddedKafka = new EmbeddedKafkaRule(1, true ,"order_topic");
		embeddedKafka.zkPort(embeddedKafkaBroker.getZkPort());
		embeddedKafka.kafkaPorts(9092);
		String brokerinfo =  embeddedKafka.getEmbeddedKafka().getBrokersAsString() ;
		System.setProperty("spring.cloud.stream.kafka.binder.brokers",brokerinfo);
	}

	@Test
	public void testCreateOrders() {
		final String uri = "/orders/produce";
		final PurchaseOrder purchaseOrder =new PurchaseOrder();
		purchaseOrder.setId(Math.round(1000f));
		purchaseOrder.setData("test data");
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<PurchaseOrder> request = new HttpEntity<PurchaseOrder>(purchaseOrder, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(uri, request, String.class);
		log.info("--------------------------------------------------------");
		String content = response.getBody();
		log.info("response content:{}", content);
		assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
		
		
		Map<String, Object> senderProps = KafkaTestUtils.producerProps(embeddedKafka.getEmbeddedKafka());
		senderProps.put("key.serializer", ByteArraySerializer.class);
		senderProps.put("value.serializer", ByteArraySerializer.class);
		DefaultKafkaProducerFactory<byte[], byte[]> pf = new DefaultKafkaProducerFactory<>(senderProps);


		Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "false", embeddedKafka.getEmbeddedKafka());
		consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		consumerProps.put("key.deserializer", ByteArrayDeserializer.class);
		consumerProps.put("value.deserializer", ByteArrayDeserializer.class);
		DefaultKafkaConsumerFactory<byte[], byte[]> cf = new DefaultKafkaConsumerFactory<>(consumerProps);

		Consumer<byte[], byte[]> consumer = cf.createConsumer();
		consumer.subscribe(Collections.singleton(this.inputTopic));
		ConsumerRecords<byte[], byte[]> records = consumer.poll(5_000);
		consumer.commitSync();

		assertThat(records.count()).isEqualTo(1);
		final String msg =new String(records.iterator().next().value());
		log.info("msg content:{}", msg);

		
		consumer.close();
		pf.destroy();
	}

}
