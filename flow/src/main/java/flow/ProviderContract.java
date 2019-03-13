package flow;

import java.util.List;

import flow.annotations.AnnotationContract;

/**
 * contract used for discovering and extracting providers from a given object.
 * 
 * @see AnnotationContract
 */
public interface ProviderContract<D extends Dependency, Prod extends Product<D>, P extends Provider<Prod, D>> {


	/**
	 * extracts one or more providers from an object
	 * @param object
	 * @return list of providers, possibly empty
	 */
	public List<P> discover(Object object)  throws FlowException;
	
	/**
	 * given some input values, a static resolver is created that is able to resolve queried dependencies during execution
	 */
	public StaticResolver<Prod, D> createResolver(List<Object> resolvables) throws FlowException;
	
}
