package it.miriade.test.cusecomlib.excep;

import it.miriade.test.cusecomlib.CuseUtil;

/**
 * Lanciata al termine di una invocazione di
 * {@link CuseUtil#findBy(it.miriade.test.cusecomlib.hooks.HtmlHook)} in caso non ci siano risultati utili.
 * 
 * @see CuseUtil#throw_ex_if_not_found
 * @author svaponi
 */
public class SeleniumElementNotFoundException extends CuseException {

	private static final long serialVersionUID = 1L;

	public SeleniumElementNotFoundException() {
		super();
	}

	public SeleniumElementNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SeleniumElementNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public SeleniumElementNotFoundException(String message) {
		super(message);
	}

	public SeleniumElementNotFoundException(Throwable cause) {
		super(cause);
	}

}
