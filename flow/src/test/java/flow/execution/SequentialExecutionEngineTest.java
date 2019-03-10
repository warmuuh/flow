package flow.execution;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import flow.FlowException;
import flow.planning.ExecutionPlanner;
import flow.planning.ExecutionPlanner.ExecutionPlan;
import flow.planning.simple.TestDependency;
import flow.planning.simple.TestProduct;
import flow.planning.simple.TestProvider;
import flow.planning.simple.TestStaticProvider;
import lombok.var;

class SequentialExecutionEngineTest {

	@Test
	void shouldExecuteAPlanAndReturnTheProduct() throws FlowException {
		var sut = new SequentialExecutionEngine<TestDependency, TestProduct, TestProvider>();
		TestProvider provider = createProvider("prov1", "dep1");
		TestProvider provider2 = createProvider("prov2", "dep2", "dep1");
		var step1 = new ExecutionPlanner.ExecutionStep<TestDependency, TestProduct, TestProvider>(provider2, emptyList());
		var step2 = new ExecutionPlanner.ExecutionStep<TestDependency, TestProduct, TestProvider>(provider, asList(step1));
		var plan = new ExecutionPlan<TestDependency, TestProduct, TestProvider>(asList(step1, step2),emptyList());
		TestProduct result = sut.execute(plan, new MapbasedResolver());
		
		assertThat(result).isEqualTo(provider.getProduct());
	}

	private TestProvider createProvider(String providerId, String createsDepId, String...dependencies) {
		var deps = Arrays.stream(dependencies).map(TestDependency::new).collect(toList());
		return new TestProvider(providerId, new TestProduct(createsDepId+"Prod"), deps, new TestDependency(createsDepId));
	}
	
	@Test
	void shouldCacheExecutionResultsAndNotCallProvidersMultipleTimes() throws FlowException {
		var sut = new SequentialExecutionEngine<TestDependency, TestProduct, TestProvider>();
		TestProvider baseProvider = Mockito.spy(createProvider("prov1", "dep1"));
		TestProvider provider1 = createProvider("prov2", "dep2", "dep1");
		TestProvider provider2 = createProvider("prov3", "dep3", "dep1");
		var step1 = new ExecutionPlanner.ExecutionStep<TestDependency, TestProduct, TestProvider>(baseProvider, emptyList());
		var step2 = new ExecutionPlanner.ExecutionStep<TestDependency, TestProduct, TestProvider>(provider1, asList(step1));
		var step3 = new ExecutionPlanner.ExecutionStep<TestDependency, TestProduct, TestProvider>(provider2, asList(step1));
		var plan = new ExecutionPlan<TestDependency, TestProduct, TestProvider>(asList(step1, step2, step3),emptyList());
		sut.execute(plan, new MapbasedResolver());
		
		Mockito.verify(baseProvider, Mockito.times(1)).invoke(emptyList());
	}
	
	@Test
	void shouldResolveProvidedInputs() throws FlowException {
		var sut = new SequentialExecutionEngine<TestDependency, TestProduct, TestProvider>();
		
		TestProvider provider1 = createProvider("prov1", "dep1", "dep2");
		TestStaticProvider provider2 = new TestStaticProvider("prov2", new TestDependency("dep2"));
		var step1 = new ExecutionPlanner.ExecutionStep<TestDependency, TestProduct, TestProvider>(provider2, asList());
		var step2 = new ExecutionPlanner.ExecutionStep<TestDependency, TestProduct, TestProvider>(provider1, asList(step1));
		var plan = new ExecutionPlan<TestDependency, TestProduct, TestProvider>(asList(step1, step2), singletonList(provider2));
		
		assertThatCode(() -> 
			sut.execute(plan, new MapbasedResolver(new TestDependency("dep2"), new TestProduct("testProduct2")))
		).doesNotThrowAnyException();
	}

	
	
	
}
