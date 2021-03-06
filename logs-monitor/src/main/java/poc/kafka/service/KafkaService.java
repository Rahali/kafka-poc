package poc.kafka.service;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import poc.kafka.properties.AppProperties;
import poc.kafka.properties.KafkaProperties;

/**
 * @author ashishb888
 */

@Service
@Slf4j
public class KafkaService {

	@Autowired
	private KafkaProperties kp;
	@Autowired
	private AppProperties ap;
	private ObjectMapper objectMapper = new ObjectMapper();
	@Autowired
	public JavaMailSender mailSender;

	private void start() {
		log.debug("start service");

		String topic = kp.getMetaData().get("topic");
		long pollMillis = Long.valueOf(kp.getMetaData().get("poll"));
		String subject = ap.getMail().get("subject");
		String from = ap.getMail().get("from");
		String[] to = ap.getMail().get("to").split("\\,");
		boolean emailEnabled = Boolean.valueOf(ap.getMail().get("enabled"));

		Consumer<Integer, String> consumer = consumer();
		consumer.subscribe(Arrays.asList(topic));

		while (true) {
			ConsumerRecords<Integer, String> records = consumer.poll(Duration.ofMillis(pollMillis));

			for (ConsumerRecord<Integer, String> consumerRecord : records) {
				try {
					JsonNode root = objectMapper.readTree(consumerRecord.value());
					JsonNode fields = root.with("fields");
					JsonNode host = root.with("host");

					String hostname = host.get("hostname").asText().toUpperCase();
					String message = root.get("message").asText();
					String app = fields.get("application").asText().toUpperCase();

					log.debug("message: " + message);
					log.debug("app: " + app);
					log.debug("hostname: " + hostname);

					if (emailEnabled)
						sendMail(MessageFormat.format(subject, app, hostname), message, from, to);

					log.debug("Mail sent");
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}

	private void sendMail(String subject, String text, String from, String... to) {
		log.debug("sendMail service");
		log.debug("to: " + Arrays.toString(to));

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(from);
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);

		mailSender.send(message);
	}

	private Consumer<Integer, String> consumer() {
		log.debug("consumer service");

		Properties kafkaProps = new Properties();

		kp.getKafkaConsumer().forEach((k, v) -> {
			log.debug("k: " + k + ", v: " + v);
			kafkaProps.put(k, v);
		});

		return new KafkaConsumer<>(kafkaProps);
	}

	public void main() {
		log.debug("main service");

		start();
	}
}
