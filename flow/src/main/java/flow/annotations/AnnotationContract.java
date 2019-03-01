package flow.annotations;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import flow.ProviderContract;
import flow.typebased.MethodCallingProvider;
import flow.typebased.TypeBasedDependency;

public class AnnotationContract implements ProviderContract<MethodCallingProvider> {

	@Override
	public List<MethodCallingProvider> discover(Object object) {
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

}
