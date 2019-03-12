package flow.typebased;

import flow.Product;
import lombok.Data;

@Data
public class ObjectRef implements Product<TypeRef>{


	private final Object object;
	
	
//	@Override
//	public boolean satisfies(TypeBasedDependency d) {
//		return false;
//	}

}
