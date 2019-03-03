package flow.typebased;

import flow.Provider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class TypeBasedProvider implements Provider<ObjectBasedProduct, TypeBasedDependency> {

	@Getter private final String id;
	@Getter private final TypeBasedDependency providingDependency;
	
}
