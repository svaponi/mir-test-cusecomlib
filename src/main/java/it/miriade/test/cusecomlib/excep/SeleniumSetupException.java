package it.miriade.test.cusecomlib.excep;

import org.openqa.selenium.WebDriver;

import it.miriade.test.cusecomlib.selenium.SeleniumWebDriverWrapper;

/**
 * Eccezioni lanciate durante il setup del {@link WebDriver}, ovvero durante la creazione di un
 * {@link SeleniumWebDriverWrapper}.
 * 
 * @author svaponi
 */
public class SeleniumSetupException extends CuseException {

	private static final long serialVersionUID = 1L;

	public SeleniumSetupException() {
		super();
	}

	public SeleniumSetupException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SeleniumSetupException(String message, Throwable cause) {
		super(message, cause);
	}

	public SeleniumSetupException(String message) {
		super(message);
	}

	public SeleniumSetupException(Throwable cause) {
		super(cause);
	}

}
