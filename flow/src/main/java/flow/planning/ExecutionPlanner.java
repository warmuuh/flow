package flow.planning;

import static java.util.stream.Collectors.toList;

import java.util.List;

import flow.Dependency;
import flow.FlowException;
import flow.Product;
import flow.Provider;
import lombok.Data;
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
	ExecutionPlan<D, Prod, P> planExecution(List<P> providers, List<D> inputs, D dep) throws FlowException;
	
	
	/**
	 * a plan that describes the steps to be executed in the right order so that for each step, the dependend ones are already executed before
	 */
	@Value
	public class ExecutionPlan<D extends Dependency, Prod extends Product<D>, P extends Provider<Prod, D>> {
		private final List<Step<D, Prod, P>> steps;
	}


	@Data
	public abstract class Step<D extends Dependency, Prod extends Product<D>, P extends Provider<Prod, D>> {
		/**
		 * list of execution steps that have to be executed before in exactly the order needed 
		 */
		final List<Step<D, Prod, P>> dependentExecutionSteps;
		
		public abstract String shortDesc();

		public abstract D getProvidingDependency(); 
		
	}
	
	@Data
	public class ExecutionStep<D extends Dependency, Prod extends Product<D>, P extends Provider<Prod, D>> extends Step<D, Prod, P> {
		P provider;

		public ExecutionStep(P provider, List<Step<D, Prod, P>> dependentExecutionSteps) {
			super(dependentExecutionSteps);
			this.provider = provider;
		}
		
		public String toString() {
			List<String> deps = dependentExecutionSteps.stream().map(s -> s.shortDesc()).collect(toList());
			return "ExecStep["+provider.getId()+"](deps:"+deps+")";
		}

		@Override
		public String shortDesc() {
			return provider.getId();
		}

		@Override
		public D getProvidingDependency() {
			return provider.getProvidingDependency();
		}
		
	}
	
	@Data
	public class InputStep<D extends Dependency, Prod extends Product<D>, P extends Provider<Prod, D>> extends Step<D, Prod, P> {
		D inputDependency;

		public InputStep(D inputDependency, List<Step<D, Prod, P>> dependentExecutionSteps) {
			super(dependentExecutionSteps);
			this.inputDependency = inputDependency;
		}
		
		public String toString() {
			List<String> deps = dependentExecutionSteps.stream().map(s -> s.shortDesc()).collect(toList());
			return "InputStep["+inputDependency+"](deps:"+deps+")";
		}

		@Override
		public String shortDesc() {
			return inputDependency.toString();
		}
		
		@Override
		public D getProvidingDependency() {
			return inputDependency;
		}
	}

}
