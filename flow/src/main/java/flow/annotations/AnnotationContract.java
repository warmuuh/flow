package flow.annotations;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import static java.util.stream.Collectors.*;

import flow.Provider;
import flow.ProviderContract;

public class AnnotationContract implements ProviderContract {

	@Override
	public List<Provider> discover(Object object) {
		return Arrays.stream(object.getClass().getMethods())
				.filter(this::isValidMethods)
				.map(t -> extractProvider(object, t))
				.collect(toList());
	}

	private Provider extractProvider(Object object, Method method) {
		return new MethodReferencingProvider(object, method);
	}

	private boolean isValidMethods(Method method) {
		return method.getAnnotation(Flower.class) != null;		
	}

}
