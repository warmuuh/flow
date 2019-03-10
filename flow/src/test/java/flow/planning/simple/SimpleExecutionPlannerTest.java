package flow.planning.simple;


import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.speedment.common.combinatorics.Permutation;

import flow.FlowException;
import flow.planning.ExecutionPlanner.ExecutionStep;
import lombok.var;

class SimpleExecutionPlannerTest {

	@Test
	void shouldT() throws FlowException {
		var planner = new SimpleExecutionPlanner<TestDependency, TestProduct, TestProvider, TestStaticProvider>();
		assertThatThrownBy(() -> planner.planExecution(emptyList(),emptyList(), new TestDependency("dep1")))
			.isInstanceOf(FlowException.class);
	}

	@Test
	void shouldReturnSingleExecutionStepIfSingleProvider() throws FlowException {
		var planner = new SimpleExecutionPlanner<TestDependency, TestProduct, TestProvider, TestStaticProvider>();
		TestProvider testProvider = new TestProvider("dep1Prov", new TestProduct("dep1Prod"), emptyList(), new TestDependency("dep1"));
		var executionPlan = planner.planExecution(singletonList(testProvider),emptyList(), new TestDependency("dep1"));
		
		assertThat(executionPlan.getSteps()).hasSize(1);
		assertThat(executionPlan.getSteps()).contains(new ExecutionStep<TestDependency, TestProduct, TestProvider>(testProvider, emptyList()));
	}

	@Test
	void shouldThrowFlowExeptionIfDependencyCannotBeFound() throws FlowException {
		var planner = new SimpleExecutionPlanner<TestDependency, TestProduct, TestProvider, TestStaticProvider>();
		TestProvider testProvider = new TestProvider("dep1Prov", new TestProduct("dep1Prod"), singletonList(new TestDependency("unknown")), new TestDependency("dep1"));

		assertThatThrownBy(() -> planner.planExecution(singletonList(testProvider),emptyList(), new TestDependency("dep1")))
			.isInstanceOf(FlowException.class);
	}
	
	@Test
	void shouldReturnTwoDependendStepsInRightOrder() throws FlowException {
		TestProvider testProvider1 = new TestProvider("dep1Prov", new TestProduct("dep1Prod"), emptyList(), new TestDependency("dep1"));
		TestProvider testProvider2 = new TestProvider("dep2Prov", new TestProduct("dep2Prod"), singletonList(new TestDependency("dep1")), new TestDependency("dep2"));
		Permutation.of( testProvider1, testProvider2 )
		.forEach(providers -> assertThatCode(() -> { 
			var planner = new SimpleExecutionPlanner<TestDependency, TestProduct, TestProvider, TestStaticProvider>();
			var executionPlan1 = planner.planExecution(providers.collect(toList()),emptyList(), new TestDependency("dep2"));
			var step1 = new ExecutionStep<TestDependency, TestProduct, TestProvider>(testProvider1, emptyList());
			var step2 = new ExecutionStep<TestDependency, TestProduct, TestProvider>(testProvider2, singletonList(step1));
			assertThat(executionPlan1.getSteps()).containsExactly(step1, step2);		
		}).doesNotThrowAnyException());
	}
	
	
	
	@Test
	void shouldReturnTransitivelyDependendStepsInRightOrder() throws FlowException {
		TestProvider testProvider1 = new TestProvider("dep1Prov", new TestProduct("dep1Prod"), emptyList(), new TestDependency("dep1"));
		TestProvider testProvider2 = new TestProvider("dep2Prov", new TestProduct("dep2Prod"), singletonList(new TestDependency("dep1")), new TestDependency("dep2"));
		TestProvider testProvider3 = new TestProvider("dep3Prov", new TestProduct("dep3Prod"), singletonList(new TestDependency("dep2")), new TestDependency("dep3"));
		Permutation.of( testProvider1, testProvider2, testProvider3)
		.forEach(providers -> assertThatCode(() -> { 
			var planner = new SimpleExecutionPlanner<TestDependency, TestProduct, TestProvider, TestStaticProvider>();
			var executionPlan1 = planner.planExecution(providers.collect(toList()),emptyList(), new TestDependency("dep3"));
			var step1 = new ExecutionStep<TestDependency, TestProduct, TestProvider>(testProvider1, emptyList());
			var step2 = new ExecutionStep<TestDependency, TestProduct, TestProvider>(testProvider2, singletonList(step1));
			var step3 = new ExecutionStep<TestDependency, TestProduct, TestProvider>(testProvider3, singletonList(step2));
			assertThat(executionPlan1.getSteps()).containsExactly(step1, step2, step3);		
		}).doesNotThrowAnyException());
	}
	
	@Test
	void shouldNotExecuteUnnecessarySteps() throws FlowException {
		TestProvider testProvider1 = new TestProvider("dep1Prov", new TestProduct("dep1Prod"), emptyList(), new TestDependency("dep1"));
		TestProvider testProvider2 = new TestProvider("dep2Prov", new TestProduct("dep2Prod"), singletonList(new TestDependency("dep1")), new TestDependency("dep2"));
		TestProvider testProvider3 = new TestProvider("dep3Prov", new TestProduct("dep3Prod"), singletonList(new TestDependency("dep2")), new TestDependency("dep3"));
		Permutation.of( testProvider1, testProvider2, testProvider3)
		.forEach(providers -> assertThatCode(() -> { 
			var planner = new SimpleExecutionPlanner<TestDependency, TestProduct, TestProvider, TestStaticProvider>();
			var executionPlan1 = planner.planExecution(providers.collect(toList()),emptyList(), new TestDependency("dep2"));
			var step1 = new ExecutionStep<TestDependency, TestProduct, TestProvider>(testProvider1, emptyList());
			var step2 = new ExecutionStep<TestDependency, TestProduct, TestProvider>(testProvider2, singletonList(step1));
			assertThat(executionPlan1.getSteps()).containsExactly(step1, step2);		
		}).doesNotThrowAnyException());
	}
	
	
	@Test
	void shouldThrowExceptionOnCircularReference() throws FlowException {
		TestProvider testProvider1 = new TestProvider("dep1Prov", new TestProduct("dep1Prod"),  singletonList(new TestDependency("dep3")), new TestDependency("dep1"));
		TestProvider testProvider2 = new TestProvider("dep2Prov", new TestProduct("dep2Prod"), singletonList(new TestDependency("dep1")), new TestDependency("dep2"));
		TestProvider testProvider3 = new TestProvider("dep3Prov", new TestProduct("dep3Prod"), singletonList(new TestDependency("dep2")), new TestDependency("dep3"));
		Permutation.of( testProvider1, testProvider2, testProvider3)
		.forEach(providers -> assertThatThrownBy(() -> { 
			var planner = new SimpleExecutionPlanner<TestDependency, TestProduct, TestProvider, TestStaticProvider>();
			planner.planExecution(providers.collect(toList()),emptyList(), new TestDependency("dep2"));
		}).isInstanceOf(FlowException.class));
	}
	
	@Test
	void shouldThrowExceptionOnMissingProvider() throws FlowException {
		TestProvider testProvider1 = new TestProvider("dep1Prov", new TestProduct("dep1Prod"),  singletonList(new TestDependency("dep3")), new TestDependency("dep1"));
		TestProvider testProvider2 = new TestProvider("dep2Prov", new TestProduct("dep2Prod"), singletonList(new TestDependency("dep1")), new TestDependency("dep2"));
		Permutation.of( testProvider1, testProvider2)
		.forEach(providers -> assertThatThrownBy(() -> { 
			var planner = new SimpleExecutionPlanner<TestDependency, TestProduct, TestProvider, TestStaticProvider>();
			planner.planExecution(providers.collect(toList()),emptyList(), new TestDependency("dep2"));
		}).isInstanceOf(FlowException.class));
	}
	
	
	@Test
	void shouldTakeStaticProvidersIntoAccount() throws FlowException {
		TestStaticProvider testProvider1 = new TestStaticProvider("dep1Prov", new TestDependency("dep1"));
		TestProvider testProvider2 = new TestProvider("dep2Prov", new TestProduct("dep2Prod"), singletonList(new TestDependency("dep1")), new TestDependency("dep2"));
		var planner = new SimpleExecutionPlanner<TestDependency, TestProduct, TestProvider, TestStaticProvider>();
		var executionPlan = planner.planExecution(singletonList(testProvider2),singletonList(testProvider1), new TestDependency("dep2"));
		var step1 = new ExecutionStep<TestDependency, TestProduct, TestProvider>(testProvider1, emptyList());
		var step2 = new ExecutionStep<TestDependency, TestProduct, TestProvider>(testProvider2, singletonList(step1));
		assertThat(executionPlan.getSteps()).containsExactly(step1, step2);		
		assertThat(executionPlan.getInputs()).containsExactly(testProvider1);
	}
	
	
	
}
