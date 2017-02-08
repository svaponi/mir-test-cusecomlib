package it.miriade.test.cusecomlib.excep;

import it.miriade.test.cusecomlib.CuseUtil;

/**
 * Lanciata quando il polling arriva al termine
 * 
 * @see CuseUtil#polling(it.miriade.test.cusecomlib.times.WhileTrueAction, Object...)
 * @see CuseUtil#pollingWithLog(it.miriade.test.cusecomlib.times.WhileTrueAction, String, Object...)
 * @author svaponi
 */
public class PollingTimeoutException extends CuseException {

	private static final long serialVersionUID = 1L;

	public PollingTimeoutException() {
		super();
	}

	public PollingTimeoutException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PollingTimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

	public PollingTimeoutException(String message) {
		super(message);
	}

	public PollingTimeoutException(Throwable cause) {
		super(cause);
	}

}
