package flow.typebased;

import flow.Product;
import lombok.Data;

@Data
public class ObjectBasedProduct implements Product<TypeBasedDependency>{


	private final Object object;
	
	
//	@Override
//	public boolean satisfies(TypeBasedDependency d) {
//		return false;
//	}

}
