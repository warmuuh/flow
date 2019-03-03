package flow.planning.simple;

import flow.Product;
import lombok.Data;

@Data public class TestProduct implements Product<TestDependency>{
//		private final boolean satisfies;
//
//		@Override
//		public boolean satisfies(TestDependency d) {return satisfies;}
		private final String id;
	}