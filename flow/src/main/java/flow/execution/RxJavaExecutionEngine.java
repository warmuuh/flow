package flow.execution;

import static java.util.Collections.emptyList;

import java.util.LinkedList;
import java.util.List;

import flow.Dependency;
import flow.FlowException;
import flow.Product;
import flow.Provider;
import flow.planning.ExecutionPlanner.ExecutionStep;
import lombok.SneakyThrows;
import rx.Single;

/**
 * Execution engine that uses RxJava to execute providers.
 * Steps will be executed in parallel and RxJava handles necessary waiting for dependent steps.
 * 
 */
public class RxJavaExecutionEngine<D extends Dependency, Prod extends Product<D>, P extends Provider<Prod, D>> extends AbstractExecutionEngine<Single<Prod>, D, Prod, P> {
	
	@Override
	protected Single<Prod> executeStep(ExecutionStep<D, Prod, P> step, List<Single<Prod>> dependencies) throws FlowException {
		Single<List<Prod>> deps;
		if (dependencies.isEmpty())
			deps = Single.just(emptyList());
		else
			deps = Single.zip(dependencies, this::convertArrayToTypedList);
		
		return deps.map(ds -> executeStepInternal(step, ds)).cache();
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> convertArrayToTypedList(Object[] results) {
		List<T> castedObjects = new LinkedList<T>();
		for(Object r : results)
			castedObjects.add((T)r);
		return castedObjects;
	}

	@SneakyThrows
	private Prod executeStepInternal(ExecutionStep<D, Prod, P> step, List<Prod> params) {
		return step.getProvider().invoke(params);
	}

	@Override
	protected Single<Prod> wrapInputValue(Prod input) { 
		return Single.just(input);
	}

}
