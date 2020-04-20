package poc.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;
import poc.kafka.service.KafkaStreamsService;

/**
 * @author ashishb888
 */

@SpringBootApplication
@Slf4j
public class KafkaStreamsApp implements CommandLineRunner {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private KafkaStreamsService kss;

	public static void main(String[] args) {
		SpringApplication.run(KafkaStreamsApp.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		logger.debug("run service");

		kss.main();
	}

}
