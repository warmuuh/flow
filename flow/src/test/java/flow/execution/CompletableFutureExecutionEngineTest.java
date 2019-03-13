package flow.execution;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import flow.FlowException;
import flow.planning.ExecutionPlanner;
import flow.planning.ExecutionPlanner.ExecutionPlan;
import flow.planning.simple.TestDependency;
import flow.planning.simple.TestProduct;
import flow.planning.simple.TestProvider;
import lombok.var;

class CompletableFutureExecutionEngineTest {


	@Test
	void shouldExecuteAPlanAndReturnTheProduct() throws FlowException {
		var sut = new CompletableFutureExecutionEngine<TestDependency, TestProduct, TestProvider>();
		TestProvider provider = createProvider("prov1", "dep1");
		TestProvider provider2 = createProvider("prov2", "dep2", "dep1");
		var step1 = new ExecutionPlanner.ExecutionStep<TestDependency, TestProduct, TestProvider>(provider2, emptyList());
		var step2 = new ExecutionPlanner.ExecutionStep<TestDependency, TestProduct, TestProvider>(provider, asList(step1));
		var plan = new ExecutionPlan<TestDependency, TestProduct, TestProvider>(asList(step1, step2));
		CompletableFuture<TestProduct> result = sut.execute(plan, new MapbasedResolver());
		
		assertThat(result.join()).isEqualTo(provider.getProduct());
	}

	private TestProvider createProvider(String providerId, String createsDepId, String...dependencies) {
		var deps = Arrays.stream(dependencies).map(TestDependency::new).collect(toList());
		return new TestProvider(providerId, new TestProduct(createsDepId+"Prod"), deps, new TestDependency(createsDepId));
	}
	
	@Test
	void shouldCacheExecutionResultsAndNotCallProvidersMultipleTimes() throws FlowException {
		var sut = new CompletableFutureExecutionEngine<TestDependency, TestProduct, TestProvider>();
		TestProvider baseProvider = Mockito.spy(createProvider("prov1", "dep1"));
		TestProvider provider1 = createProvider("prov2", "dep2", "dep1");
		TestProvider provider2 = createProvider("prov3", "dep3", "dep1");
		var step1 = new ExecutionPlanner.ExecutionStep<TestDependency, TestProduct, TestProvider>(baseProvider, emptyList());
		var step2 = new ExecutionPlanner.ExecutionStep<TestDependency, TestProduct, TestProvider>(provider1, asList(step1));
		var step3 = new ExecutionPlanner.ExecutionStep<TestDependency, TestProduct, TestProvider>(provider2, asList(step1));
		var plan = new ExecutionPlan<TestDependency, TestProduct, TestProvider>(asList(step1, step2, step3));
		sut.execute(plan, new MapbasedResolver()).join();
		
		Mockito.verify(baseProvider, Mockito.times(1)).invoke(emptyList());
	}
	
}
