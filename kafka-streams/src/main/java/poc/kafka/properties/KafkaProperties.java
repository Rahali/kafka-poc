package poc.kafka.properties;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "kafka")
@Setter
@Getter
@SuppressWarnings("unused")
public class KafkaProperties {
	private Map<String, String> kafkaStreams;
}
