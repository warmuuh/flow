package flow;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import flow.annotations.AnnotationContract;
import flow.annotations.Flower;
import flow.execution.RxJavaExecutionEngine;
import flow.execution.SequentialExecutionEngine;
import flow.typebased.ObjectRef;
import flow.typebased.TypeRef;
import lombok.var;
import rx.Single;
import rx.observers.TestSubscriber;


class FlowTest {

	public static class InputObject{ }
	public static class SelfProvider1 {
		@Flower
		public SelfProvider1 execute(InputObject object) {
			return this;
		}
	}
	public static class SelfProvider2 {
		@Flower
		public SelfProvider2 execute(SelfProvider1 provider1) {
			return this;
		}
	}
	
	
	@Test
	void shouldTakeProvidedInputIntoAccount() throws FlowException {
		var flow = new Flow<>(new AnnotationContract(), new SequentialExecutionEngine<>());
		SelfProvider1 provider1 = new SelfProvider1();
		SelfProvider2 provider2 = new SelfProvider2();
		flow.registerProviders(provider1, provider2);
		
		var plan = flow.planExecution(new TypeRef(SelfProvider2.class), new TypeRef(InputObject.class));
		ObjectRef result = flow.executePlan(plan, new InputObject());
		assertThat(result.getObject()).isEqualTo(provider2);
	}
	
	
	@Test
	void shouldWorkForRxJava() throws FlowException {
		var flow = new Flow<>(new AnnotationContract(), new RxJavaExecutionEngine<>());
		SelfProvider1 provider1 = new SelfProvider1();
		SelfProvider2 provider2 = new SelfProvider2();
		flow.registerProviders(provider1, provider2);
		
		var plan = flow.planExecution(new TypeRef(SelfProvider2.class), new TypeRef(InputObject.class));
		Single<ObjectRef> result = flow.executePlan(plan, new InputObject());
		
		var subscriber = new TestSubscriber<ObjectRef>();
		result.subscribe(subscriber);
		subscriber.awaitTerminalEvent();
		subscriber.assertNoErrors();
		subscriber.assertCompleted();
		subscriber.assertValue(new ObjectRef(provider2));
	}


}
