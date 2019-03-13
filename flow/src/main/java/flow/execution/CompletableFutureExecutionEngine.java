package flow.execution;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import flow.Dependency;
import flow.FlowException;
import flow.Product;
import flow.Provider;
import flow.planning.ExecutionPlanner.ExecutionStep;
import lombok.SneakyThrows;

public class CompletableFutureExecutionEngine <D extends Dependency, Prod extends Product<D>, P extends Provider<Prod, D>>
extends AbstractExecutionEngine<CompletableFuture<Prod>, D, Prod, P>{

	@Override
	protected CompletableFuture<Prod> executeStep(ExecutionStep<D, Prod, P> step, List<CompletableFuture<Prod>> dependencies) throws FlowException {
		CompletableFuture<List<Prod>> dependenciesFuture = dependencies.isEmpty() 
																	? CompletableFuture.completedFuture(emptyList()) 
																	: allOf(dependencies);
		return dependenciesFuture.thenApply(deps -> executeStepInternal(step, deps));
	}

	@Override
	protected CompletableFuture<Prod> wrapInputValue(Prod input) {
		return CompletableFuture.completedFuture(input);
	}

	
	@SneakyThrows
	private Prod executeStepInternal(ExecutionStep<D, Prod, P> step, List<Prod> params) {
		return step.getProvider().invoke(params);
	}
	
	
	public <T> CompletableFuture<List<T>> allOf(List<CompletableFuture<T>> futuresList) {
	    CompletableFuture<Void> allFuturesResult =
	    CompletableFuture.allOf(futuresList.toArray(new CompletableFuture[] {}));
	    return allFuturesResult.thenApply(v ->
	            futuresList.stream()
	                    .map(future -> future.join())
	                    .collect(toList())
	    );
	}

}
