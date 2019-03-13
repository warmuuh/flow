package flow.typebased;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import flow.FlowException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * a provider implementation that wraps around a method invocation.
 * 
 */
@EqualsAndHashCode(callSuper=true)
@ToString
public class MethodCallingProvider extends TypeBasedProvider {

	@ToString.Exclude private final Object object;
	private final Method method;
	@Getter private final List<TypeRef> dependencies;
	
	
	public MethodCallingProvider(Object object, Method method, List<TypeRef> dependencies) {
		super(method.getDeclaringClass().getSimpleName() + "." + method.getName(), constructReturnTypeRef(method));
		this.object = object;
		this.method = method;
		this.dependencies = dependencies;
	}


	private static TypeRef constructReturnTypeRef(Method method) {
		return new TypeRef(method.getGenericReturnType());
	}
	
	
	@Override
	public ObjectRef invoke(List<ObjectRef> products) throws FlowException {
		Object[] arguments = constructArguments(products);
		Object result = invokeMethod(arguments);
		return new ObjectRef(result);
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

	private Object[] constructArguments(List<ObjectRef> products) {
		return products.stream().map(ObjectRef::getObject).collect(Collectors.toList()).toArray();
	}
	
}
