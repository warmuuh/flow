package flow;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import flow.annotations.AnnotationContract;
import flow.annotations.Flower;
import flow.execution.SequentialExecutionEngine;
import flow.typebased.GenericObjectRef;
import flow.typebased.GenericTypeRef;
import flow.typebased.ObjectRef;
import lombok.var;


class GenericFlowTest {

	public static class DoubleProvider {
		@Flower
		public List<Double> execute(List<String> object) {
			return object.stream().map(Double::parseDouble).collect(toList());
		}
	}
	
	@Test
	void shouldBeAbleToHandleGenericParameters() throws FlowException {
		var flow = new Flow<>(new AnnotationContract(), new SequentialExecutionEngine<>());
		flow.registerProviders(new DoubleProvider());
		
		var plan = flow.planExecution(new GenericTypeRef<List<Double>>() {}, new GenericTypeRef<List<String>>() {});
		List<String> strings = Arrays.asList("0", "1", "2");
		ObjectRef result = flow.executePlan(plan, new GenericObjectRef<List<String>>(strings) {});
		assertThat((List<Double>)result.getObject()).containsExactly(0.0, 1.0, 2.0);
	}
	
	


}
