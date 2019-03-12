package flow;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import flow.annotations.AnnotationContract;
import flow.annotations.Flower;
import flow.execution.SequentialExecutionEngine;
import flow.typebased.ObjectBasedProduct;
import flow.typebased.TypeBasedDependency;
import lombok.var;


class FlowTest {

	public static class InputObject{
		
	}
	
	
	public static class SelfProvider1 {
		@Flower
		public SelfProvider1 providing(InputObject object) {
			return this;
		}
	}
	
	
	public static class SelfProvider2 {
		@Flower
		public SelfProvider2 providing(SelfProvider1 provider1) {
			return this;
		}
	}
	
	
	@Test
	void shouldTakeProvidedInputIntoAccount() throws FlowException {
		var flow = new Flow<>(new AnnotationContract(), new SequentialExecutionEngine<>());
		SelfProvider2 provider2 = new SelfProvider2();
		flow.registerProviders(new SelfProvider1(), provider2);
		
		var plan = flow.planExecution(new TypeBasedDependency(SelfProvider2.class), new TypeBasedDependency(InputObject.class));
		ObjectBasedProduct result = flow.executePlan(plan, new InputObject());
		assertThat(result.getObject()).isEqualTo(provider2);
	}

}
