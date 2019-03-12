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
import flow.typebased.ObjectBasedProduct;
import flow.typebased.TypeBasedDependency;
import flow.typebased.TypeBasedProvider;

public class AnnotationContract implements ProviderContract<TypeBasedDependency, ObjectBasedProduct, TypeBasedProvider> {

	@Override
	public List<TypeBasedProvider> discover(Object object) {
		return Arrays.stream(object.getClass().getMethods()).filter(this::isValidMethods)
				.map(t -> extractProvider(object, t)).collect(toList());
	}

	private MethodCallingProvider extractProvider(Object object, Method method) {
		List<TypeBasedDependency> dependencies = getDependencies(method);
		return new MethodCallingProvider(object, method, dependencies);
	}

	private boolean isValidMethods(Method method) {
		return method.getAnnotation(Flower.class) != null;
	}

	private List<TypeBasedDependency> getDependencies(Method method) {
		return Arrays.stream(method.getParameters())
				.map(p -> new TypeBasedDependency(p.getType()))
				.collect(Collectors.toList());
	}


	@Override
	public StaticResolver<ObjectBasedProduct, TypeBasedDependency> createResolver(List<Object> resolvables) throws FlowException {
		Map<TypeBasedDependency, Object> mapping = resolvables.stream().collect(Collectors.toMap(o -> new TypeBasedDependency(o.getClass()), o -> o));
		return new StaticResolver<ObjectBasedProduct, TypeBasedDependency>() {
			
			@Override
			public ObjectBasedProduct resolve(TypeBasedDependency providingDependency) {
				return new ObjectBasedProduct(mapping.get(providingDependency));
			}
			
			@Override
			public boolean canResolve(TypeBasedDependency dependency) {
				return mapping.containsKey(dependency);
			}
		};
	}

}
