package flow.typebased;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import flow.FlowException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper=true)
public class MethodCallingProvider extends TypeBasedProvider {

	private final Object object;
	private final Method method;
	@Getter private final List<TypeBasedDependency> dependencies;
	
	
	public MethodCallingProvider(Object object, Method method, List<TypeBasedDependency> dependencies) {
		super(method.getDeclaringClass().getSimpleName() + "." + method.getName(), new TypeBasedDependency(method.getReturnType()));
		this.object = object;
		this.method = method;
		this.dependencies = dependencies;
	}
	
	
	@Override
	public ObjectBasedProduct invoke(List<ObjectBasedProduct> products) throws FlowException {
		Object[] arguments = constructArguments(products);
		Object result = invokeMethod(arguments);
		return new ObjectBasedProduct(result);
	}

	private Object invokeMethod(Object[] arguments) throws FlowException {
		try {
			return method.invoke(object, arguments);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new FlowException("Failed to invoke provider", e);
		} catch (Throwable e) {
			throw new FlowException("Provider threw an Exception", e);
		}
	}

	private Object[] constructArguments(List<ObjectBasedProduct> products) {
		return products.stream().map(ObjectBasedProduct::getObject).collect(Collectors.toList()).toArray();
	}
	
}
