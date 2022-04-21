package com.online.store.demo.ordereventdriven.stream;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Map;

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
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.online.store.demo.ordereventdriven.model.Message;
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
public class OrderEventConsumerStreamServiceTest {
	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka ;
	@Autowired
	private EmbeddedKafkaBroker embeddedKafkaBroker;
	@Autowired
	private ObjectMapper objectMapper;
	
//	@Value("${spring.cloud.stream.bindings.event-producer.destination}")
//	private String inputTopic;

	@Value("${spring.cloud.stream.bindings.event-consumer.destination}")
	private String outputTopic;
	
	@BeforeEach
	protected void setUp() throws Exception {
		embeddedKafka = new EmbeddedKafkaRule(1, true ,"order_topic");
		embeddedKafka.zkPort(embeddedKafkaBroker.getZkPort());
		embeddedKafka.kafkaPorts(9092);
		String brokerinfo =  embeddedKafka.getEmbeddedKafka().getBrokersAsString() ;
		System.setProperty("spring.cloud.stream.kafka.binder.brokers",brokerinfo);
	}

	@Test
	public void testConsumeEvent() throws JsonMappingException, JsonProcessingException {
		Map<String, Object> senderProps = KafkaTestUtils.producerProps(embeddedKafka.getEmbeddedKafka());
		senderProps.put("key.serializer", ByteArraySerializer.class);
		senderProps.put("value.serializer", ByteArraySerializer.class);
		DefaultKafkaProducerFactory<byte[], byte[]> pf = new DefaultKafkaProducerFactory<>(senderProps);
		KafkaTemplate<byte[], byte[]> template = new KafkaTemplate<byte[], byte[]>(pf, true);
		template.setDefaultTopic(outputTopic);
		Message msg =new Message();
		msg.setId(Math.round(1000f));
		msg.setData("test data");
		msg.setBytePayload("test data".getBytes());		
		template.sendDefault(objectMapper.writeValueAsBytes(msg) );
		

		Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "false", embeddedKafka.getEmbeddedKafka());
		consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		consumerProps.put("key.deserializer", ByteArrayDeserializer.class);
		consumerProps.put("value.deserializer", ByteArrayDeserializer.class);
		DefaultKafkaConsumerFactory<byte[],byte[]> cf = new DefaultKafkaConsumerFactory<>(consumerProps);

		Consumer<byte[], byte[]> consumer = cf.createConsumer();
		consumer.subscribe(Collections.singleton(this.outputTopic));
		ConsumerRecords<byte[], byte[]> records = consumer.poll(5_000);
		consumer.commitSync();

		assertThat(records.count()).isEqualTo(1);
		final String msgContent =new String(records.iterator().next().value());
		Message gotMsg =  objectMapper.readValue(msgContent, Message.class);
		assertThat(gotMsg.getData()).isEqualTo("test data");
		
		consumer.close();
		pf.destroy();
	}

}
