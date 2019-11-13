package poc.kafka.service;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import poc.kafka.common.serialization.StockTradeDeserializer;
import poc.kafka.common.serialization.StockTradeSerializer;
import poc.kafka.domain.StockTrade;
import poc.kafka.properties.KafkaProperties;

/**
 * @author ashishb888
 */

@Service
@Slf4j
@SuppressWarnings("unused")
public class KafkaStreamsService {

	@Autowired
	private KafkaProperties kp;

	private void stream() throws Exception {
		log.debug("stream service");

		final StreamsBuilder builder = new StreamsBuilder();
		String topic = kp.getMetaData().get("topic");

		KStream<String, StockTrade> source = builder.stream(topic, Consumed.with(Serdes.String(),
				Serdes.serdeFrom(new StockTradeSerializer(), new StockTradeDeserializer())));

		KTable<String, Double> result = source.map((k, v) -> KeyValue.pair(k, v.getTotTrdVal())).groupByKey()
				.reduce((aggVal, newVal) -> aggVal < newVal ? newVal : aggVal);

		log.debug("queryableStoreName: " + result.queryableStoreName());

		result.toStream().peek((k, v) -> {
			log.debug("k: " + k + ", v:" + v);
		}).to(topic + "-out", Produced.with(Serdes.String(), Serdes.Double()));

		final Topology topology = builder.build();
		final KafkaStreams streams = new KafkaStreams(topology, configs());
		final CountDownLatch latch = new CountDownLatch(1);

		log.info("topology: " + topology.describe());

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			streams.close();
			latch.countDown();
		}, "streams-shutdown-hook"));

		try {
			streams.start();
			latch.await();
		} catch (Throwable e) {
			log.error("", e);
		}
	}

	private void start() {
		log.debug("start service");

		try {
			stream();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private Properties configs() {
		Properties configs = new Properties();

		kp.getKafkaStreams().forEach((k, v) -> {
			configs.put(k, v);
		});

		return configs;
	}

	public void main() {
		log.debug("main service");

		start();
	}
}