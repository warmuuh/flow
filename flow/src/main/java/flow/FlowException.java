package flow;

public class FlowException extends Exception {

	private static final long serialVersionUID = 4122061425353035888L;

	public FlowException(String message, Throwable cause) {
		super(message, cause);
	}

	public FlowException(String message) {
		super(message);
	}

}
