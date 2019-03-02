package flow;

/**
 * represents the result a provider creates.
 *
 */
public interface Product<D extends Dependency> {
	
	/**
	 * test is this product satisfies a given dependency
	 * @param d the dependency to test
	 * @return true if the dependency is satisfied by this product
	 */
//	boolean satisfies(D d);
}