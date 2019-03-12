package flow.execution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import flow.Dependency;
import flow.FlowException;
import flow.Product;
import flow.Provider;
import flow.StaticResolver;
import flow.planning.ExecutionPlanner.ExecutionPlan;
import flow.planning.ExecutionPlanner.ExecutionStep;
import flow.planning.ExecutionPlanner.InputStep;
import flow.planning.ExecutionPlanner.Step;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public abstract class AbstractExecutionEngine<T, D extends Dependency, Prod extends Product<D>, P extends Provider<Prod, D>>  implements ExecutionEngine<T, D, Prod, P> {

	@Override
	public T execute(ExecutionPlan<D, Prod, P> plan, StaticResolver<Prod, D> staticResolver) throws FlowException {

		Map<Step<D, Prod, P>, T> results = new HashMap<>();
		
		T lastProd = null;
		for (Step<D, Prod, P> step : plan.getSteps()) {
			log.info("Executing step: " + step);
			List<T> dependencies = step.getDependentExecutionSteps().stream()
				.map(dep -> getCachedResult(results, staticResolver, dep))
				.collect(Collectors.toList());
			
			T invocationResult = null;
			if (step instanceof ExecutionStep)
				invocationResult = executeStep((ExecutionStep<D, Prod, P>)step, dependencies);
			
			results.put(step, invocationResult);
			lastProd = invocationResult;
		}
		
		return lastProd;
	}

	@SneakyThrows
	private T getCachedResult(Map<Step<D, Prod, P>, T> results, StaticResolver<Prod, D> staticResolver, Step<D, Prod, P> step) {
		if (step instanceof InputStep && staticResolver.canResolve(step.getProvidingDependency()))
			return wrapInputValue(staticResolver.resolve(step.getProvidingDependency()));
		
		if (!results.containsKey(step))
			throw new FlowException("could not resolve input for "+step);
		
		return results.get(step);
	}

	protected abstract T executeStep(ExecutionStep<D, Prod, P> step, List<T> dependencies)  throws FlowException;
	
	protected abstract T wrapInputValue(Prod input);
}