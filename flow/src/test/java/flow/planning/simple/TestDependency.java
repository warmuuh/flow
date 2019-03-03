package flow.planning.simple;

import flow.Dependency;
import lombok.Data;

@Data public class TestDependency implements Dependency{ 	
	private final String id;
	public String toString() {
		return "D["+id+"]";
	}
}