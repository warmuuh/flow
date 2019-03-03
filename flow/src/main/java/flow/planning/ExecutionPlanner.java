package flow.planning;

import static java.util.stream.Collectors.toList;

import java.util.List;

import flow.Dependency;
import flow.FlowException;
import flow.Product;
import flow.Provider;
import lombok.Value;

/**
 * an execution planner is an algorithm that figures out the sequence of how to invoke providers
 * and what results to reuse so that the least possible providers are executed for a given type
 * 
 */
public interface ExecutionPlanner<D extends Dependency, Prod extends Product<D>, P extends Provider<Prod, D>> {


	/**
	 * Plans the execution steps
	 * @param dep the dependency that finally should be created
	 * @return a plan that describes what steps need to be executed in what order
	 * @throws FlowException
	 */
	ExecutionPlan<D, Prod, P> planExecution(List<P> providers, D dep) throws FlowException;
	
	
	/**
	 * a plan that describes the steps to be executed in the right order so that for each step, the dependend ones are already executed before
	 */
	@Value
	public class ExecutionPlan<D extends Dependency, Prod extends Product<D>, P extends Provider<Prod, D>> {
		private final List<ExecutionStep<D, Prod, P>> steps;
	}

	@Value
	public class ExecutionStep<D extends Dependency, Prod extends Product<D>, P extends Provider<Prod, D>> {
		P provider;
		/**
		 * list of execution steps that have to be executed before in exactly the order needed 
		 */
		List<ExecutionStep<D, Prod, P>> dependentExecutionSteps;
		
		public String toString() {
			List<String> deps = dependentExecutionSteps.stream().map(s -> s.getProvider().getId()).collect(toList());
			return "Step["+provider.getId()+"](deps:"+deps+")";
		}
	}

}
