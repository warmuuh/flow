package flow;

import java.util.List;

public interface ProviderContract<D extends Dependency, Prod extends Product<D>, P extends Provider<Prod, D>, S extends Provider<Prod,D>& StaticProvider<Prod, D>> {

	
	public List<P> discover(Object object);
	
	public S providerForInput(D object);
	
	public StaticResolver<Prod, D> createResolver(List<Object> resolvables) throws FlowException;
	
}
