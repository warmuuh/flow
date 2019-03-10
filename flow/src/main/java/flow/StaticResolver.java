package flow;

import flow.typebased.TypeBasedDependency;

public interface StaticResolver<P extends Product<D>, D extends Dependency> {

	P resolve(D providingDependency);

}
