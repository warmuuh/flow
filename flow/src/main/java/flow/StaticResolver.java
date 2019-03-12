package flow;

/**
 * a static resolver for resolving queried dependencies against given input values.
 * 
 */
public interface StaticResolver<P extends Product<D>, D extends Dependency> {

	P resolve(D providingDependency);

	boolean canResolve(D dependency);
	
}
