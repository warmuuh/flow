package flow;

import java.util.List;

public interface ProviderContract<P extends Provider> {

	
	public List<P> discover(Object object);
	
}
