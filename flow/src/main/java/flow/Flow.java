package flow;

import static java.util.Arrays.asList;

import java.util.LinkedList;
import java.util.List;

import flow.execution.ExecutionEngine;
import flow.planning.ExecutionPlanner;
import flow.planning.ExecutionPlanner.ExecutionPlan;
import flow.planning.simple.SimpleExecutionPlanner;

public class Flow<T, D extends Dependency, Prod extends Product<D>, P extends Provider<Prod, D>, S extends Provider<Prod,D>& StaticProvider<Prod, D>> {

	private final ProviderContract<D, Prod, P, S> contract;
	private final ExecutionPlanner<D, Prod, P, S> executionPlanner = new SimpleExecutionPlanner<D, Prod, P, S>();
	private final ExecutionEngine<T, D, Prod, P> executionEngine;
	private List<P> registeredProviders = new LinkedList<>();
	
	
	
	public Flow(ProviderContract<D, Prod, P, S> contract, ExecutionEngine<T, D, Prod, P> executionEngine) {
		super();
		this.contract = contract;
		this.executionEngine = executionEngine;
	}


	
	public Flow<T, D, Prod, P, S> registerProviders(Object...providerObjects) {
		List<P> newProviders = new LinkedList<>();
		for(Object po : providerObjects)
			newProviders.addAll(contract.discover(po));
		this.registeredProviders = newProviders;
		return this;
	}
	
	
	public ExecutionPlan<D, Prod, P> planExecution(D queriedDependency, D...providedDependencies) throws FlowException {
		List<S> inputProviders = new LinkedList<>();
		for(D providedDependency : providedDependencies)
			inputProviders.add(contract.providerForInput(providedDependency));
		
		return executionPlanner.planExecution(registeredProviders, inputProviders, queriedDependency);
	}
	
	public T executePlan(ExecutionPlan<D, Prod, P> plan, Object...providedInputs) throws FlowException {
		StaticResolver<Prod, D> resolver = contract.createResolver(asList(providedInputs));
		return executionEngine.execute(plan, resolver);
	}
	
	
}
