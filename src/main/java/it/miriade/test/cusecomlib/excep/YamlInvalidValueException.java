package it.miriade.test.cusecomlib.excep;

/**
 * Indica che è il valore della key immessa non è valido oppure è nullo
 * 
 * @author svaponi
 */
public class YamlInvalidValueException extends CuseException {
	private static final long serialVersionUID = 1L;

	public YamlInvalidValueException() {
		super();
	}

	public YamlInvalidValueException(String message) {
		super(message);
	}
}
