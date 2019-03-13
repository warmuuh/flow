package flow.typebased;

import java.lang.reflect.Type;

import lombok.Getter;
import lombok.ToString;

@ToString
public class GenericObjectRef<T> extends ObjectRef {

	@Getter private final Type objectType;
	
	public GenericObjectRef(T object) {
		super(object);
		objectType = ReflectionUtil.extractTypeParameter(getClass());
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}
	
}
