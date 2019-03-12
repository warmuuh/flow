package flow;

import java.util.List;

public interface ProviderContract<D extends Dependency, Prod extends Product<D>, P extends Provider<Prod, D>> {

	
	public List<P> discover(Object object);
	
	public StaticResolver<Prod, D> createResolver(List<Object> resolvables) throws FlowException;
	
}
