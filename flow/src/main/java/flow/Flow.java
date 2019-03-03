package flow;

import java.util.LinkedList;
import java.util.List;

import flow.execution.ExecutionEngine;
import flow.planning.ExecutionPlanner;
import flow.planning.ExecutionPlanner.ExecutionPlan;
import flow.planning.simple.SimpleExecutionPlanner;

public class Flow<T, D extends Dependency, Prod extends Product<D>, P extends Provider<Prod, D>> {

	private final ProviderContract<D, Prod, P> contract;
	private final ExecutionPlanner<D, Prod, P> executionPlanner = new SimpleExecutionPlanner<D, Prod, P>();
	private final ExecutionEngine<T, D, Prod, P> executionEngine;
	private List<P> registeredProviders = new LinkedList<>();
	
	
	
	public Flow(ProviderContract<D, Prod, P> contract, ExecutionEngine<T, D, Prod, P> executionEngine) {
		super();
		this.contract = contract;
		this.executionEngine = executionEngine;
	}


	
	public Flow<T, D, Prod, P> registerProviders(Object...providerObjects) {
		List<P> newProviders = new LinkedList<>();
		for(Object po : providerObjects)
			newProviders.addAll(contract.discover(po));
		this.registeredProviders = newProviders;
		return this;
	}
	
	
	public ExecutionPlan<D, Prod, P> planExecution(D queriedDependency, Object...inputObjects) throws FlowException {
		List<P> providers = new LinkedList<P>(registeredProviders);
		for(Object input : inputObjects)
			providers.add(contract.providerForInput(input));
		
		return executionPlanner.planExecution(providers, queriedDependency);
	}
	
	public T executePlan(ExecutionPlan<D, Prod, P> plan) throws FlowException {
		return executionEngine.execute(plan);
	}
	
	
}
