package flow.planning.simple;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
public class SimpleExecutionPlanner<D extends Dependency, Prod extends Product<D>, P extends Provider<Prod, D>> 
implements ExecutionPlanner<D, Prod, P> {


	@Override
	public ExecutionPlan<D, Prod, P> planExecution(List<P> providers, List<D> inputs, D queriedDependency) throws FlowException {
		List<Step<D, Prod, P>> steps = calculateSteps(providers, inputs, queriedDependency);
		return new ExecutionPlan<D, Prod, P>(steps);
	}

	private List<Step<D, Prod, P>> calculateSteps(List<P> providers, List<D> inputs, D queriedDependency)  throws FlowException {
		var sortedProviders = new LinkedList<P>();
		collectOrderedProviders(queriedDependency, providers, inputs, sortedProviders);
		Collections.reverse(sortedProviders);
		List<Step<D, Prod, P>> result = new LinkedList<>();
		
		for(P provider : sortedProviders) {
			
			List<Step<D, Prod, P>> dependendExecutionSteps = provider.getDependencies().stream()
			.map(d -> findExecutionStepsForDependency(result, inputs, d))
			.collect(toList());
			
			var newStep = new ExecutionStep<D, Prod, P>(provider, dependendExecutionSteps);
			result.add(newStep);
		}
		return result;
	}

	@SneakyThrows
	private void collectOrderedProviders(D curDependency, List<P> providers, List<D> inputs, List<P> sortedProviders) throws FlowException{
		if (inputs.contains(curDependency))
			return;
		
		var provider = providers.stream().filter(p -> p.getProvidingDependency().equals(curDependency))
						.findAny()
						.orElseThrow(() -> new FlowException("Cannot find provider for dependency " + curDependency));
		
		if (sortedProviders.contains(provider))
			throw new FlowException("Circular reference found including dependency " + curDependency);
		
		sortedProviders.add(provider);
		
		provider.getDependencies().forEach(d -> recurseIntoOrdering(provider, providers, inputs, sortedProviders, d));
	}

	@SneakyThrows
	private void recurseIntoOrdering(P provider, List<P> providers, List<D> inputs, List<P> sortedProviders, D d) {
		try {
			collectOrderedProviders(d, providers, inputs, sortedProviders);
		} catch (FlowException e) {
			throw new FlowException("Error collecting dependencies for provider " + provider);
		}
	}
	
	@SneakyThrows
	private Step<D, Prod, P> findExecutionStepsForDependency(List<Step<D, Prod, P>> steps, List<D> inputs, D d)  {
		Optional<Step<D, Prod, P>> previousStep = steps.stream()
				.filter(step -> step.getProvidingDependency().equals(d))
				.findAny();
		
		if (previousStep.isPresent())
			return previousStep.get();
		
		Optional<InputStep<D, Prod, P>> inputStep = inputs.stream()
				.filter(i -> i.equals(d))
				.map(i -> new InputStep<D, Prod, P>(i, emptyList()))
				.findAny();
		
		
		return inputStep
				//this should not happen as it is guarded in collectOrderedProvider already
				.orElseThrow(() -> new FlowException("could not find provider for dependency " + d + " in previous execution steps nor in inputs.")); 
	}
}
