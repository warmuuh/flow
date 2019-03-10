package flow.planning.simple;

import static java.util.Collections.emptyList;

import java.util.List;

import flow.FlowException;
import flow.StaticProvider;
import flow.StaticResolver;

public class TestStaticProvider extends TestProvider implements StaticProvider<TestProduct, TestDependency>{

	
	private StaticResolver<TestProduct, TestDependency> resolver;

	public TestStaticProvider(String id, TestDependency providingDependency) {
		super(id, null, emptyList(), providingDependency);
	}

	@Override
	public TestProduct invoke(List<TestProduct> satisfiedDependencies) throws FlowException {
		return resolver.resolve(getProvidingDependency());
	}
	
	
	@Override
	public void setResolver(StaticResolver<TestProduct, TestDependency> resolver) {
		this.resolver = resolver;
		
	}
	
}