package flow.typebased;

public class GenericTypeRef<T> extends TypeRef {

	public GenericTypeRef() {
		super(null); //can't refer to this in super-call
		genericType = ReflectionUtil.extractTypeParameter(getClass());
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}
	

	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
}
