package flow.planning.simple;

import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import flow.Dependency;
import flow.FlowException;
import flow.Product;
import flow.Provider;
import flow.planning.ExecutionPlanner;
import lombok.SneakyThrows;
import lombok.var;

/**
 * a simple execution planner that just orders the provider based on their dependencies.
 * If provider A depends on B, B will be before A. Unnecessary providers wont be used.
 * 
 * Implications:
 * * cannot decide between alternatives: a->b->c and a->d->g->c.
 * * chosen alternative is based on sequence-order of given providers
 *
 */
public class SimpleExecutionPlanner<D extends Dependency, Prod extends Product<D>, P extends Provider<Prod, D>> implements ExecutionPlanner<D, Prod, P> {


	@Override
	public ExecutionPlan<D, Prod, P> planExecution(List<P> providers, D queriedDependency) throws FlowException {
		
		List<ExecutionStep<D, Prod, P>> steps = calculateSteps(providers, queriedDependency);
		return new ExecutionPlan<D, Prod, P>(steps);
	}

	private List<ExecutionStep<D, Prod, P>> calculateSteps(List<P> providers, D queriedDependency) {
		var sortedProviders = new LinkedList<P>();
		collectOrderedProviders(queriedDependency, providers, sortedProviders);
		Collections.reverse(sortedProviders);
		List<ExecutionStep<D, Prod, P>> result = new LinkedList<>();
		
		for(P provider : sortedProviders) {
			
			List<ExecutionStep<D, Prod, P>> dependendExecutionSteps = provider.getDependencies().stream()
			.map(d -> findExecutionStepsForDependency(result, d))
			.collect(toList());
			
			var newStep = new ExecutionStep<D, Prod, P>(provider, dependendExecutionSteps);
			result.add(newStep);
		}
		return result;
	}

	@SneakyThrows
	private void collectOrderedProviders(D curDependency, List<P> providers, List<P> sortedProviders) {
		var provider = providers.stream().filter(p -> p.getProvidingDependency().equals(curDependency))
						.findAny().orElseThrow(() -> new FlowException("Cannot find provider for dependency " + curDependency));
		
		if (sortedProviders.contains(provider))
			throw new FlowException("Circular reference found including dependency " + curDependency);
		
		sortedProviders.add(provider);
		
		provider.getDependencies().forEach(d -> collectOrderedProviders(d, providers, sortedProviders));
	}
	
	@SneakyThrows
	private ExecutionStep<D, Prod, P> findExecutionStepsForDependency(List<ExecutionStep<D, Prod, P>> steps, D d)  {
		return steps.stream()
				.filter(step -> step.getProvider().getProvidingDependency().equals(d))
				.findAny()
				.orElseThrow(() -> new FlowException("could not find provider for dependency " + d + " in previous execution steps."));
	}
}
