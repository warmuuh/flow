package flow;

import java.util.List;

/**
 * a provider encapsulates the logic that something can be created based on some dependencies 
 *
 */
public interface Provider<P extends Product, D extends Dependency> {

	String getId();

	/**
	 * runs the provider to create a product 
	 * @param satisfiedDependencies all products that are needed for creating the result
	 * @return the created product
	 * @throws FlowException if exception is thrown or dependencies did not match
	 */
	P invoke(List<P> satisfiedDependencies) throws FlowException;

	/**
	 * @return the list of dependencies that the provider has
	 */
	List<D> getDependencies();
	
	
	
	/**
	 * @return the dependency that this provider would fullfill if invoked
	 */
	D getProvidingDependency();
}
