package flow.typebased;

import flow.Provider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * a type-based provider uses a type-reference for providing dependency 
 *
 */
@RequiredArgsConstructor
public abstract class TypeBasedProvider implements Provider<ObjectRef, TypeRef> {

	@Getter private final String id;
	@Getter private final TypeRef providingDependency;
	
}
