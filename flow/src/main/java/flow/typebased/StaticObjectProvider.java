package flow.typebased;

import static java.util.Collections.emptyList;

import java.util.List;

import flow.FlowException;

public class StaticObjectProvider extends TypeBasedProvider {

	private final Object object;
	
	public StaticObjectProvider(Object object) {
		super(object.getClass().getSimpleName(), new TypeBasedDependency(object.getClass()));
		this.object = object;
	}

	@Override
	public ObjectBasedProduct invoke(List<ObjectBasedProduct> satisfiedDependencies) throws FlowException {
		return new ObjectBasedProduct(object);
	}

	@Override
	public List<TypeBasedDependency> getDependencies() {
		return emptyList();
	}

}
