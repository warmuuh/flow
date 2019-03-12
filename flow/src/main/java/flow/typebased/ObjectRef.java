package flow.typebased;

import flow.Product;
import lombok.Data;

/**
 * small wrapper around an object reference
 */
@Data
public class ObjectRef implements Product<TypeRef>{
	private final Object object;
}
