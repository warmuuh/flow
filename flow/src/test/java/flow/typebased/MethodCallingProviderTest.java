package flow.typebased;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import flow.FlowException;
import lombok.val;

public class MethodCallingProviderTest {

	public static interface TestClass {
		String noParamMethod() throws Exception;
		String stringParamDep(String string) throws Exception;
	}
	@Test
	void shouldCreateProperId() throws NoSuchMethodException, SecurityException {
		TestClass obj = Mockito.mock(TestClass.class);
		val provider = createProvider(obj, "noParamMethod");
		assertThat(provider.getId()).isEqualTo("TestClass.noParamMethod");
	}

	
	@Nested
	public class Invokation {

		@Test
		void shouldInvokeNoParamMethodCorrectly() throws Exception {
			TestClass obj = mock(TestClass.class);
			when(obj.noParamMethod()).thenReturn("testValue");
			val provider = createProvider(obj, "noParamMethod");
			val result = provider.invoke(emptyList());
			verify(obj).noParamMethod();
			assertThat(result.getObject()).isEqualTo("testValue");
		}

		@Test
		void shouldInvokeParamMethodCorrectly() throws Exception {
			TestClass obj = mock(TestClass.class);
			when(obj.stringParamDep(anyString())).thenReturn("testValue");
			val provider = createProvider(obj, "stringParamDep");
			provider.invoke(asList(new ObjectRef("stringParamValue")));
			verify(obj).stringParamDep("stringParamValue");
		}

		
		@Test
		void shouldThrowFlowExceptionIfParameterTypeDoesNotMatch() throws Exception {
			TestClass obj = mock(TestClass.class);
			when(obj.stringParamDep(anyString())).thenReturn("testValue");
			val provider = createProvider(obj, "stringParamDep");
			
			assertThatThrownBy(() -> provider.invoke(asList(new ObjectRef(Integer.valueOf(123)))) )
			.isInstanceOf(FlowException.class);
		}
		
		@Test
		void shouldCatchMethodExceptionsCorrectly() throws Exception {
			TestClass obj = mock(TestClass.class);
			doThrow(new Exception("test")).when(obj).noParamMethod();
			val provider = createProvider(obj, "noParamMethod");
			assertThatThrownBy(() -> provider.invoke(emptyList())).isInstanceOf(FlowException.class);
		}
	}

	
	@Test
	void shouldCorrectlyCheckFullFillment() throws Exception {
		TestClass obj = mock(TestClass.class);
		val provider = createProvider(obj, "noParamMethod");
		assertThat(provider.getProvidingDependency()).isEqualTo(new TypeRef(String.class));
	}

	
	private MethodCallingProvider createProvider(TestClass obj, String methodName) throws NoSuchMethodException {
		
		Method method = Arrays.stream(TestClass.class.getMethods())
		.filter(m -> m.getName().equals(methodName))
		.findAny().orElseThrow(() -> new NoSuchMethodException("TestFailure"));
		
		return new MethodCallingProvider(obj, method, emptyList());
	}
	
}
