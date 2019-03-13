package flow.annotations;


import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import flow.FlowException;
import flow.typebased.GenericTypeRef;
import flow.typebased.TypeBasedProvider;
import flow.typebased.TypeRef;
import lombok.val;

class AnnotationContractTest {

	
	public static class NoProviders {
		public void SomeMethod() {}; 
	}
	public static class InvalidProvider {
		public @Flower void SomeMethod() { };
	}
	
	public static class SimpleProvider {
		public @Flower String SomeMethod() { return "";};
		public @Flower String SomeOtherMethod() { return "";};
	}
	
	public static class GenericProvider {
		public @Flower List<String> SomeMethod() { return emptyList();};
		public @Flower List<Double> SomeOtherMethod(List<String> parameter) { return emptyList();};
	} 
	
	public static class SingleParamProvider {
		public @Flower String SomeParamMethod(String dependency) {return "";};
	}
	
	
	public static class InheritedClass extends SimpleProvider { }
	
	@Test
	void returnEmptyListIfNoProvidersFound() {
		val sut = new AnnotationContract();
		val providers = sut.discover(new NoProviders());
		assertTrue(providers.isEmpty());
	}
	
	
	@Test
	void findAllAnnotatatedProviders() {
		val sut = new AnnotationContract();
		val providers = sut.discover(new SimpleProvider());
		assertFalse(providers.isEmpty());
		assertThat(providers).extracting(p -> p.getId()).contains("SimpleProvider.SomeMethod", "SimpleProvider.SomeOtherMethod");
	}
	
	@Test
	void findAllAnnotatedProvidersInSuperClass() {
		val sut = new AnnotationContract();
		val providers = sut.discover(new InheritedClass());
		assertFalse(providers.isEmpty());
		assertThat(providers).extracting(p -> p.getId()).contains("SimpleProvider.SomeMethod", "SimpleProvider.SomeOtherMethod");
	}
	
	@Test
	void shouldExtractCorrectDependenciesOfProviders() {
		val sut = new AnnotationContract();
		val providers = sut.discover(new SingleParamProvider());
		
		assertThat(providers)
			.flatExtracting(p -> p.getDependencies())
			.containsExactly(new TypeRef(String.class));
	}
	
	@Test
	void shouldExtractCorrectTypeOfProviders() {
		val sut = new AnnotationContract();
		val providers = sut.discover(new SingleParamProvider());
		
		assertThat(providers)
			.extracting(p -> p.getProvidingDependency())
			.containsOnly(new TypeRef(String.class));
	}
	
	@Test
	void shouldFailForInvalidProviders() {
		val sut = new AnnotationContract();
		
		assertThatThrownBy(() -> sut.discover(new InvalidProvider()))
			.isInstanceOf(FlowException.class);
			
	}
	
	@Test
	void shouldTakeGenericParameterIntoAccount() {
		val sut = new AnnotationContract();
		
		val providers = sut.discover(new GenericProvider());

		assertThat(providers.get(0).getProvidingDependency())
			.isNotEqualTo(providers.get(1).getProvidingDependency());
		
		
		TypeBasedProvider provider = providers.stream().filter(p -> p.getId().equals("GenericProvider.SomeOtherMethod")).findFirst().get();		
		assertThat(provider.getDependencies())
			.containsExactly(new GenericTypeRef<List<String>>() {});
	}
}
