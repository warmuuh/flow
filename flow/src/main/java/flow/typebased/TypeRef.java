package flow.typebased;

import flow.Dependency;
import lombok.Data;

@Data
public class TypeRef implements Dependency {

	private final Class<?> type;
	
}
