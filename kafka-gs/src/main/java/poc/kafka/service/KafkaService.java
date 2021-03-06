package poc.kafka.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.java.Log;

/**
 * @author ashishb888
 */

@Service
@Log
@SuppressWarnings("unused")
public class KafkaService {

	@Autowired
	private ExternalizableService es;
	@Autowired
	private SerializableService ss;
	@Autowired
	private ConsumerService cs;

	public void main() {
		log.info("main service");

		// es.main();
		// ss.main();
		cs.main();
	}
}
