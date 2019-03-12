package flow;

public interface StaticResolver<P extends Product<D>, D extends Dependency> {

	P resolve(D providingDependency);

	boolean canResolve(D dependency);
	
}
