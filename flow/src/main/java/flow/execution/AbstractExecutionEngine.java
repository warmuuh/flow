package flow.execution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import flow.Dependency;
import flow.FlowException;
import flow.Product;
import flow.Provider;
import flow.planning.ExecutionPlanner.ExecutionPlan;
import flow.planning.ExecutionPlanner.ExecutionStep;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractExecutionEngine<T, D extends Dependency, Prod extends Product<D>, P extends Provider<Prod, D>>  implements ExecutionEngine<T, D, Prod, P> {

	@Override
	public T execute(ExecutionPlan<D, Prod, P> plan) throws FlowException {

		Map<ExecutionStep<D, Prod, P>, T> results = new HashMap<>();
		
		T lastProd = null;
		for (ExecutionStep<D, Prod, P> step : plan.getSteps()) {
			log.info("Executing step: " + step);
			List<T> dependencies = step.getDependentExecutionSteps().stream()
				.map(dep -> results.get(dep))
				.collect(Collectors.toList());
			T invocationResult = executeStep(step, dependencies);
			results.put(step, invocationResult);
			lastProd = invocationResult;
		}
		
		return lastProd;
	}

	protected abstract T executeStep(ExecutionStep<D, Prod, P> step, List<T> dependencies)  throws FlowException;
}