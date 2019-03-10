package flow.execution;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import flow.FlowException;
import flow.planning.ExecutionPlanner;
import flow.planning.ExecutionPlanner.ExecutionPlan;
import flow.planning.simple.TestDependency;
import flow.planning.simple.TestProduct;
import flow.planning.simple.TestProvider;
import flow.typebased.TypeBasedDependency;
import lombok.var;
import rx.Single;
import rx.observers.TestSubscriber;

class RxJavaExecutionEngineTest {

	
	@Test
	void shouldExecuteAPlanAndReturnTheProduct() throws FlowException {
		var sut = new RxJavaExecutionEngine<TestDependency, TestProduct, TestProvider>();
		TestProvider provider = createProvider("prov1", "dep1");
		TestProvider provider2 = createProvider("prov2", "dep2", "dep1");
		var step1 = new ExecutionPlanner.ExecutionStep<TestDependency, TestProduct, TestProvider>(provider2, emptyList());
		var step2 = new ExecutionPlanner.ExecutionStep<TestDependency, TestProduct, TestProvider>(provider, asList(step1));
		var plan = new ExecutionPlan<TestDependency, TestProduct, TestProvider>(asList(step1, step2),emptyList());
		Single<TestProduct> result = sut.execute(plan, new MapbasedResolver());
		
		
		var subscriber = new TestSubscriber<TestProduct>();
		result.subscribe(subscriber);
		subscriber.awaitTerminalEvent();
		subscriber.assertNoErrors();
		subscriber.assertCompleted();
		subscriber.assertValue(provider.getProduct());
	}

	private TestProvider createProvider(String providerId, String createsDepId, String...dependencies) {
		var deps = Arrays.stream(dependencies).map(TestDependency::new).collect(toList());
		return new TestProvider(providerId, new TestProduct(createsDepId+"Prod"), deps, new TestDependency(createsDepId));
	}
	
	@Test
	void shouldCacheExecutionResultsAndNotCallProvidersMultipleTimes() throws FlowException {
		var sut = new RxJavaExecutionEngine<TestDependency, TestProduct, TestProvider>();
		TestProvider baseProvider = Mockito.spy(createProvider("prov1", "dep1"));
		TestProvider provider1 = createProvider("prov2", "dep2", "dep1");
		TestProvider provider2 = createProvider("prov3", "dep3", "dep1");
		TestProvider provider3 = createProvider("prov4", "dep4", "dep2", "dep3");
		var step1 = new ExecutionPlanner.ExecutionStep<TestDependency, TestProduct, TestProvider>(baseProvider, emptyList());
		var step2 = new ExecutionPlanner.ExecutionStep<TestDependency, TestProduct, TestProvider>(provider1, asList(step1));
		var step3 = new ExecutionPlanner.ExecutionStep<TestDependency, TestProduct, TestProvider>(provider2, asList(step1));
		var step4 = new ExecutionPlanner.ExecutionStep<TestDependency, TestProduct, TestProvider>(provider3, asList(step2, step3));
		var plan = new ExecutionPlan<TestDependency, TestProduct, TestProvider>(asList(step1, step2, step3, step4),emptyList());

		
		Single<TestProduct> result = sut.execute(plan, new MapbasedResolver());
		var subscriber = new TestSubscriber<TestProduct>();
		result.subscribe(subscriber);
		subscriber.awaitTerminalEvent();
		subscriber.assertNoErrors();
		subscriber.assertCompleted();
		subscriber.assertValue(provider3.getProduct());
		
		Mockito.verify(baseProvider, Mockito.times(1)).invoke(emptyList());
	}
	
}
