package flow.annotations;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import flow.FlowException;
import flow.Provider;
import lombok.Data;

@Data
public class MethodReferencingProvider implements Provider {

	private final Object object;
	private final Method method;
	
	@Override
	public String getId() {
		return method.getDeclaringClass().getSimpleName() + "." + method.getName();
	}

	@Override
	public void invoke() throws FlowException {
		try {
			method.invoke(object, new Object[] {});
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new FlowException("Failed to invoke provider", e);
		} catch (Throwable e) {
			throw new FlowException("Provider threw an Exception", e);
		}
	}
	
}
