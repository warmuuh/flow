package flow.execution.simple;


import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.speedment.common.combinatorics.Permutation;

import flow.Dependency;
import flow.FlowException;
import flow.Product;
import flow.Provider;
import flow.execution.ExecutionPlanner.ExecutionStep;
import lombok.Data;
import lombok.var;

class SimpleExecutionPlannerTest {

	@Data
	static class TestDependency implements Dependency{ 	
		private final String id;
		public String toString() {
			return "D["+id+"]";
		}
	}

	
	@Data
	static class TestProduct implements Product<TestDependency>{
//		private final boolean satisfies;
//
//		@Override
//		public boolean satisfies(TestDependency d) {return satisfies;}
		
	}
	
	@Data
	static class TestProvider implements Provider<TestProduct, TestDependency>{

		private final String id; 
		private final TestProduct product;
		private final List<TestDependency> dependencies;
		private final TestDependency providingDependency;

		@Override
		public TestProduct invoke(List<TestProduct> satisfiedDependencies) throws FlowException {
			return product;
		}
		public String toString() {
			return "P["+id+"]("+dependencies + ")";
		}
		
	}
	
	
	@Test
	void shouldT() throws FlowException {
		var planner = new SimpleExecutionPlanner<TestDependency, TestProduct, TestProvider>();
		assertThatThrownBy(() -> planner.planExecution(emptyList(), new TestDependency("dep1")))
			.isInstanceOf(FlowException.class);
	}

	@Test
	void shouldReturnSingleExecutionStepIfSingleProvider() throws FlowException {
		var planner = new SimpleExecutionPlanner<TestDependency, TestProduct, TestProvider>();
		TestProvider testProvider = new TestProvider("dep1Prov", new TestProduct(), emptyList(), new TestDependency("dep1"));
		var executionPlan = planner.planExecution(singletonList(testProvider), new TestDependency("dep1"));
		
		assertThat(executionPlan.getSteps()).hasSize(1);
		assertThat(executionPlan.getSteps()).contains(new ExecutionStep<TestDependency, TestProduct, TestProvider>("step[dep1Prov]", testProvider, emptyList()));
	}

	@Test
	void shouldThrowFlowExeptionIfDependencyCannotBeFound() throws FlowException {
		var planner = new SimpleExecutionPlanner<TestDependency, TestProduct, TestProvider>();
		TestProvider testProvider = new TestProvider("dep1Prov", new TestProduct(), singletonList(new TestDependency("unknown")), new TestDependency("dep1"));

		assertThatThrownBy(() -> planner.planExecution(singletonList(testProvider), new TestDependency("dep1")))
			.isInstanceOf(FlowException.class);
	}
	
	@Test
	void shouldReturnTwoDependendStepsInRightOrder() throws FlowException {
		TestProvider testProvider1 = new TestProvider("dep1Prov", new TestProduct(), emptyList(), new TestDependency("dep1"));
		TestProvider testProvider2 = new TestProvider("dep2Prov", new TestProduct(), singletonList(new TestDependency("dep1")), new TestDependency("dep2"));
		Permutation.of( testProvider1, testProvider2 )
		.forEach(providers -> assertThatCode(() -> { 
			var planner = new SimpleExecutionPlanner<TestDependency, TestProduct, TestProvider>();
			var executionPlan1 = planner.planExecution(providers.collect(toList()), new TestDependency("dep2"));
			var step1 = new ExecutionStep<TestDependency, TestProduct, TestProvider>("step[dep1Prov]", testProvider1, emptyList());
			var step2 = new ExecutionStep<TestDependency, TestProduct, TestProvider>("step[dep2Prov]", testProvider2, singletonList(step1));
			assertThat(executionPlan1.getSteps()).containsExactly(step1, step2);		
		}).doesNotThrowAnyException());
	}
	
	
	
	@Test
	void shouldReturnTransitivelyDependendStepsInRightOrder() throws FlowException {
		TestProvider testProvider1 = new TestProvider("dep1Prov", new TestProduct(), emptyList(), new TestDependency("dep1"));
		TestProvider testProvider2 = new TestProvider("dep2Prov", new TestProduct(), singletonList(new TestDependency("dep1")), new TestDependency("dep2"));
		TestProvider testProvider3 = new TestProvider("dep3Prov", new TestProduct(), singletonList(new TestDependency("dep2")), new TestDependency("dep3"));
		Permutation.of( testProvider1, testProvider2, testProvider3)
		.forEach(providers -> assertThatCode(() -> { 
			var planner = new SimpleExecutionPlanner<TestDependency, TestProduct, TestProvider>();
			var executionPlan1 = planner.planExecution(providers.collect(toList()), new TestDependency("dep3"));
			var step1 = new ExecutionStep<TestDependency, TestProduct, TestProvider>("step[dep1Prov]", testProvider1, emptyList());
			var step2 = new ExecutionStep<TestDependency, TestProduct, TestProvider>("step[dep2Prov]", testProvider2, singletonList(step1));
			var step3 = new ExecutionStep<TestDependency, TestProduct, TestProvider>("step[dep3Prov]", testProvider3, singletonList(step2));
			assertThat(executionPlan1.getSteps()).containsExactly(step1, step2, step3);		
		}).doesNotThrowAnyException());
	}
	
	@Test
	void shouldNotExecuteUnnecessarySteps() throws FlowException {
		TestProvider testProvider1 = new TestProvider("dep1Prov", new TestProduct(), emptyList(), new TestDependency("dep1"));
		TestProvider testProvider2 = new TestProvider("dep2Prov", new TestProduct(), singletonList(new TestDependency("dep1")), new TestDependency("dep2"));
		TestProvider testProvider3 = new TestProvider("dep3Prov", new TestProduct(), singletonList(new TestDependency("dep2")), new TestDependency("dep3"));
		Permutation.of( testProvider1, testProvider2, testProvider3)
		.forEach(providers -> assertThatCode(() -> { 
			var planner = new SimpleExecutionPlanner<TestDependency, TestProduct, TestProvider>();
			var executionPlan1 = planner.planExecution(providers.collect(toList()), new TestDependency("dep2"));
			var step1 = new ExecutionStep<TestDependency, TestProduct, TestProvider>("step[dep1Prov]", testProvider1, emptyList());
			var step2 = new ExecutionStep<TestDependency, TestProduct, TestProvider>("step[dep2Prov]", testProvider2, singletonList(step1));
			assertThat(executionPlan1.getSteps()).containsExactly(step1, step2);		
		}).doesNotThrowAnyException());
	}
	
	
	@Test
	void shouldThrowExceptionOnCircularReference() throws FlowException {
		TestProvider testProvider1 = new TestProvider("dep1Prov", new TestProduct(),  singletonList(new TestDependency("dep3")), new TestDependency("dep1"));
		TestProvider testProvider2 = new TestProvider("dep2Prov", new TestProduct(), singletonList(new TestDependency("dep1")), new TestDependency("dep2"));
		TestProvider testProvider3 = new TestProvider("dep3Prov", new TestProduct(), singletonList(new TestDependency("dep2")), new TestDependency("dep3"));
		Permutation.of( testProvider1, testProvider2, testProvider3)
		.forEach(providers -> assertThatThrownBy(() -> { 
			var planner = new SimpleExecutionPlanner<TestDependency, TestProduct, TestProvider>();
			planner.planExecution(providers.collect(toList()), new TestDependency("dep2"));
		}).isInstanceOf(FlowException.class));
	}
	
	@Test
	void shouldThrowExceptionOnMissingProvider() throws FlowException {
		TestProvider testProvider1 = new TestProvider("dep1Prov", new TestProduct(),  singletonList(new TestDependency("dep3")), new TestDependency("dep1"));
		TestProvider testProvider2 = new TestProvider("dep2Prov", new TestProduct(), singletonList(new TestDependency("dep1")), new TestDependency("dep2"));
		Permutation.of( testProvider1, testProvider2)
		.forEach(providers -> assertThatThrownBy(() -> { 
			var planner = new SimpleExecutionPlanner<TestDependency, TestProduct, TestProvider>();
			planner.planExecution(providers.collect(toList()), new TestDependency("dep2"));
		}).isInstanceOf(FlowException.class));
	}
	
}
