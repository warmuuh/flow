package flow.annotations;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import flow.FlowException;
import flow.ProviderContract;
import flow.StaticResolver;
import flow.typebased.GenericObjectRef;
import flow.typebased.MethodCallingProvider;
import flow.typebased.ObjectRef;
import flow.typebased.TypeBasedProvider;
import flow.typebased.TypeRef;
import lombok.SneakyThrows;

/**
 * a flow-contract that uses @Flower annotated methods to define providers. Parameters are the dependencies of the provider.
 * 
 * <pre>{@code 
 *  public static class ExampleProvider {
 *		@Flower
 *		public ProvidedType execute(InputObject object) {
 *			...
 *		}
 *	}
 * }</pre>
 * 
 *
 */
public class AnnotationContract implements ProviderContract<TypeRef, ObjectRef, TypeBasedProvider> {

	@Override
	public List<TypeBasedProvider> discover(Object object) {
		return Arrays.stream(object.getClass().getMethods()).filter(this::isValidMethods)
				.map(t -> extractProvider(object, t)).collect(toList());
	}

	@SneakyThrows
	private MethodCallingProvider extractProvider(Object object, Method method) {
		List<TypeRef> dependencies = getDependencies(method);
		
		//validation:
		if (method.getReturnType().equals(void.class))
			throw new FlowException("Void not allowed as return type: " + method);
		
		return new MethodCallingProvider(object, method, dependencies);
	}

	private boolean isValidMethods(Method method) {
		return method.getAnnotation(Flower.class) != null;
	}

	private List<TypeRef> getDependencies(Method method) {
		return Arrays.stream(method.getGenericParameterTypes())
				.map(p -> new TypeRef(p))
				.collect(Collectors.toList());
	}


	@Override
	public StaticResolver<ObjectRef, TypeRef> createResolver(List<Object> resolvables) throws FlowException {
		Map<TypeRef, Object> mapping = resolvables.stream().collect(Collectors.toMap(o -> getTypeFromObject(o), o -> getObject(o)));
		return new StaticResolver<ObjectRef, TypeRef>() {
			
			@Override
			public ObjectRef resolve(TypeRef providingDependency) {
				return new ObjectRef(mapping.get(providingDependency));
			}
			
			@Override
			public boolean canResolve(TypeRef dependency) {
				return mapping.containsKey(dependency);
			}
		};
	}

	private Object getObject(Object o) {
		if (o instanceof ObjectRef)
			return ((ObjectRef)o).getObject();
		
		return o;
	}

	private TypeRef getTypeFromObject(Object o) {
		if (o instanceof GenericObjectRef)
			return new TypeRef(((GenericObjectRef<?>)o).getObjectType());
		if (o instanceof ObjectRef)
			return new TypeRef(((ObjectRef)o).getObject().getClass());
		
		return new TypeRef(o.getClass());
	}

}
