package poc.kafka.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Schema2 {
	private String type;
	private boolean optional;
	private String field;
	private String name;

	public Schema2(String type, boolean optional, String field) {
		super();
		this.type = type;
		this.optional = optional;
		this.field = field;
	}
}
