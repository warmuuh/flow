package flow;

public interface StaticProvider<P extends Product<D>, D extends Dependency>  {

	
	public void setResolver(StaticResolver<P, D> resolver);
	
}
