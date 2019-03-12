package flow.typebased;

import flow.Dependency;
import lombok.Data;

/**
 * small wrapper around a class-type.
 */
@Data
public class TypeRef implements Dependency {

	private final Class<?> type;
	
}
