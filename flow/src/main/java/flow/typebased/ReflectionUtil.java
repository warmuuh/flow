package flow.typebased;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ReflectionUtil {

	public static Type extractTypeParameter(Class<?> type) {
		Type genericSuperclass = type.getGenericSuperclass();
		
		if (!(genericSuperclass instanceof ParameterizedType))
			throw new IllegalArgumentException("No generic supertype found. Did you miss to create a new Subclass? Usage example: \"new GenericTypeRef<...>(){}\"");  
		
		ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
		
		if (parameterizedType.getActualTypeArguments().length != 1)
			throw new IllegalArgumentException("No generic supertype found. Did you miss to create a new Subclass? Usage example: \"new GenericTypeRef<...>(){}\"");
		
		return parameterizedType.getActualTypeArguments()[0];
	}
}
