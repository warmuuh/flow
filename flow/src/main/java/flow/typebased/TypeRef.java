package flow.typebased;

import java.lang.reflect.Type;

import flow.Dependency;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * small wrapper around a class-type.
 */
@EqualsAndHashCode
@ToString
public class TypeRef implements Dependency {

	@Getter protected Type genericType; // has to be modifyable bc of GenericTypeRef

	public TypeRef(Type genericType) {
		this.genericType = genericType;
	}

}
