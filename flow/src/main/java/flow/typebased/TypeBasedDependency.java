package flow.typebased;

import flow.Dependency;
import lombok.Data;

@Data
public class TypeBasedDependency implements Dependency {

	private final Class<?> type;
	
}
