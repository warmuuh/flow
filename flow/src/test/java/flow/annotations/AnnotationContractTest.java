package flow.annotations;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import flow.typebased.TypeBasedDependency;
import lombok.val;

class AnnotationContractTest {

	
	public static class NoProviders {
		public void SomeMethod() {}; 
	}
	
	public static class SingleProvider {
		public @Flower void SomeMethod() {};
		public @Flower void SomeOtherMethod() {};
	}
	
	public static class SingleParamProvider {
		public @Flower void SomeParamMethod(String dependency) {};
	}
	
	
	public static class InheritedClass extends SingleProvider { }
	
	@Test
	void returnEmptyListIfNoProvidersFound() {
		val sut = new AnnotationContract();
		val providers = sut.discover(new NoProviders());
		assertTrue(providers.isEmpty());
	}
	
	
	@Test
	void findAllAnnotatatedProviders() {
		val sut = new AnnotationContract();
		val providers = sut.discover(new SingleProvider());
		assertFalse(providers.isEmpty());
		assertThat(providers).extracting(p -> p.getId()).contains("SingleProvider.SomeMethod", "SingleProvider.SomeOtherMethod");
	}
	
	@Test
	void findAllAnnotatedProvidersInSuperClass() {
		val sut = new AnnotationContract();
		val providers = sut.discover(new InheritedClass());
		assertFalse(providers.isEmpty());
		assertThat(providers).extracting(p -> p.getId()).contains("SingleProvider.SomeMethod", "SingleProvider.SomeOtherMethod");
	}
	
	@Test
	void shouldExtractCorrectDependenciesOfProviders() {
		val sut = new AnnotationContract();
		val providers = sut.discover(new SingleParamProvider());
		
		assertThat(providers)
			.flatExtracting(p -> p.getDependencies())
			.containsExactly(new TypeBasedDependency(String.class));
	}

}
