package it.miriade.test.cusecomlib.excep;

/**
 * Eccezioni base per tutte le altre della commonlib
 * 
 * @author svaponi
 */
public abstract class CuseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CuseException() {
		super();
	}

	public CuseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CuseException(String message, Throwable cause) {
		super(message, cause);
	}

	public CuseException(String message) {
		super(message);
	}

	public CuseException(Throwable cause) {
		super(cause);
	}

}
