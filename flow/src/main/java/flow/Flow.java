package flow;

import static java.util.Arrays.asList;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import flow.execution.ExecutionEngine;
import flow.planning.ExecutionPlanner;
import flow.planning.ExecutionPlanner.ExecutionPlan;
import flow.planning.simple.SimpleExecutionPlanner;

/**
 * Flow API facade. Example Usage:
 * <pre>{@code 
 * var flow = new Flow<>(new AnnotationContract(), new SequentialExecutionEngine<>());
 * flow.registerProviders(provider1, provider2);
 * 
 * var plan = flow.planExecution(new TypeRef(SelfProvider2.class), new TypeRef(InputObject.class));
 * ObjectRef result = flow.executePlan(plan, new InputObject());
 *	}</pre>
 * 
 *
 */

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
	
	
	
	@SuppressWarnings("unchecked")
	public ExecutionPlan<D, Prod, P> planExecution(D queriedDependency, D...providedDependencies) throws FlowException {
		return executionPlanner.planExecution(registeredProviders, Arrays.asList(providedDependencies), queriedDependency);
	}
	
	public T executePlan(ExecutionPlan<D, Prod, P> plan, Object...providedInputs) throws FlowException {
		StaticResolver<Prod, D> resolver = contract.createResolver(asList(providedInputs));
		return executionEngine.execute(plan, resolver);
	}
	
	
}
