package flow.execution;

import flow.Dependency;
import flow.FlowException;
import flow.Product;
import flow.Provider;
import flow.StaticResolver;
import flow.planning.ExecutionPlanner.ExecutionPlan;

/**
 * execution engine takes care of executing planned steps in the right order.
 * Execution engines can decide wheter to execute them delayed, in parallel, synchronized etc
 * 
 * In general, all steps are only executed once ({@link AbstractExecutionEngine}) and results are cached.
 */
public interface ExecutionEngine<T, D extends Dependency, Prod extends Product<D>, P extends Provider<Prod, D>> {

	T execute(ExecutionPlan<D, Prod, P> plan, StaticResolver<Prod, D> staticResolver) throws FlowException;
}
