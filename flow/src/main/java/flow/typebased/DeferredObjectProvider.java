package flow.typebased;

import static java.util.Collections.emptyList;

import java.util.List;

import flow.FlowException;
import flow.StaticProvider;
import flow.StaticResolver;

public class DeferredObjectProvider extends TypeBasedProvider implements StaticProvider<ObjectBasedProduct, TypeBasedDependency> {

	
	private StaticResolver<ObjectBasedProduct, TypeBasedDependency> resolver;

	public DeferredObjectProvider(TypeBasedDependency providedDependency) {
		super(providedDependency.getType().getSimpleName(), providedDependency);
	}

	@Override
	public ObjectBasedProduct invoke(List<ObjectBasedProduct> satisfiedDependencies) throws FlowException {
		return resolver.resolve(getProvidingDependency());
	}

	@Override
	public List<TypeBasedDependency> getDependencies() {
		return emptyList();
	}

	@Override
	public void setResolver(StaticResolver<ObjectBasedProduct, TypeBasedDependency> resolver) {
		this.resolver = resolver;
	}

}
