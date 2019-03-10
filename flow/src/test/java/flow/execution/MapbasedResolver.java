package flow.execution;

import java.util.HashMap;
import java.util.Map;

import flow.StaticResolver;
import flow.planning.simple.TestDependency;
import flow.planning.simple.TestProduct;
import flow.planning.simple.TestProvider;
import lombok.Getter;

public class MapbasedResolver implements StaticResolver<TestProduct, TestDependency> {
	private @Getter Map<TestDependency, TestProduct> map = new HashMap<>();
	
	public MapbasedResolver() {
	}
	
	public MapbasedResolver(TestDependency d1, TestProduct p1) {
		map.put(d1, p1);
	}
	
	@Override
	public TestProduct resolve(TestDependency providingDependency) {
		return map.get(providingDependency);
	}
}