package flow.annotations;


import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import flow.FlowException;

import static org.mockito.Mockito.*;

import lombok.val;

import static org.assertj.core.api.Assertions.*;



class MethodReferencingProviderTest {

	public static interface TestClass {
		void noParamMethod() throws Exception;
	}
	
	
	@Test
	void shouldCreateProperId() throws NoSuchMethodException, SecurityException {
		TestClass obj = Mockito.mock(TestClass.class);
		val provider = new MethodReferencingProvider(obj, TestClass.class.getMethod("noParamMethod"));
		assertThat(provider.getId()).isEqualTo("TestClass.noParamMethod");
	}
	
	@Test 
	void shouldInvokeMethodCorrectly() throws Exception {
		TestClass obj = mock(TestClass.class);
		val provider = new MethodReferencingProvider(obj, TestClass.class.getMethod("noParamMethod"));
		provider.invoke();
		verify(obj).noParamMethod();
	}
	
	@Test
	void shouldCatchMethodExceptionsCorrectly() throws Exception {
		TestClass obj = mock(TestClass.class);
		doThrow(new Exception("test")).when(obj).noParamMethod();
		val provider = new MethodReferencingProvider(obj, TestClass.class.getMethod("noParamMethod"));
		assertThatThrownBy(() -> provider.invoke()).isInstanceOf(FlowException.class);
	}

}
