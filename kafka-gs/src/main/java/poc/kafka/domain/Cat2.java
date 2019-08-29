package poc.kafka.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.java.Log;

@Log
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Cat2 extends Animal2 {

	private int logs;

	public void whoAmI() {
		log.info("I am a Cat");
	}
}
