package flow.execution;

import flow.Dependency;
import flow.FlowException;
import flow.Product;
import flow.Provider;
import flow.StaticResolver;
import flow.planning.ExecutionPlanner.ExecutionPlan;

public interface ExecutionEngine<T, D extends Dependency, Prod extends Product<D>, P extends Provider<Prod, D>> {

	T execute(ExecutionPlan<D, Prod, P> plan, StaticResolver<Prod, D> staticResolver) throws FlowException;
}
