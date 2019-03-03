package flow.planning.simple;

import java.util.List;

import flow.FlowException;
import flow.Provider;
import lombok.Data;

@Data public class TestProvider implements Provider<TestProduct, TestDependency>{

	private final String id; 
	private final TestProduct product;
	private final List<TestDependency> dependencies;
	private final TestDependency providingDependency;

	@Override
	public TestProduct invoke(List<TestProduct> satisfiedDependencies) throws FlowException {
		return product;
	}
	public String toString() {
		return "P["+id+"]("+dependencies + ")";
	}
	
}