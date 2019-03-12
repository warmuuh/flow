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
import flow.typebased.MethodCallingProvider;
import flow.typebased.ObjectRef;
import flow.typebased.TypeBasedProvider;
import flow.typebased.TypeRef;

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

	private MethodCallingProvider extractProvider(Object object, Method method) {
		List<TypeRef> dependencies = getDependencies(method);
		return new MethodCallingProvider(object, method, dependencies);
	}

	private boolean isValidMethods(Method method) {
		return method.getAnnotation(Flower.class) != null;
	}

	private List<TypeRef> getDependencies(Method method) {
		return Arrays.stream(method.getParameters())
				.map(p -> new TypeRef(p.getType()))
				.collect(Collectors.toList());
	}


	@Override
	public StaticResolver<ObjectRef, TypeRef> createResolver(List<Object> resolvables) throws FlowException {
		Map<TypeRef, Object> mapping = resolvables.stream().collect(Collectors.toMap(o -> new TypeRef(o.getClass()), o -> o));
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

}
