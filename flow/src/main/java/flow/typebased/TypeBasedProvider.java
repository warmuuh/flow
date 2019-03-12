package flow.typebased;

import flow.Provider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class TypeBasedProvider implements Provider<ObjectRef, TypeRef> {

	@Getter private final String id;
	@Getter private final TypeRef providingDependency;
	
}
