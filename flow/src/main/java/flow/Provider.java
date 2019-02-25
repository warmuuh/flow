package flow;

public interface Provider {

	String getId();

	void invoke() throws FlowException;

}
