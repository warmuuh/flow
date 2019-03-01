package flow.typebased;

import flow.Dependency;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class TypeBasedDependency implements Dependency {

	private final Class<?> type;
	
}
