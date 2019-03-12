package flow.execution;

import java.util.List;

import flow.Dependency;
import flow.FlowException;
import flow.Product;
import flow.Provider;
import flow.planning.ExecutionPlanner.ExecutionStep;

/**
 * Sequentially executes providers, caches their results to be used in
 * subsequent providers and returns the result of the final step
 */
public class SequentialExecutionEngine<D extends Dependency, Prod extends Product<D>, P extends Provider<Prod, D>>
		extends AbstractExecutionEngine<Prod, D, Prod, P> {
	
	@Override
	protected Prod executeStep(ExecutionStep<D, Prod, P> step, List<Prod> dependencies) throws FlowException {
		return step.getProvider().invoke(dependencies);
	}

	@Override
	protected Prod wrapInputValue(Prod input) {
		return input;
	}

	

}
