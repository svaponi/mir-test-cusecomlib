package it.miriade.test.cusecomlib.excep;

/**
 * Indica che è stato richiesto un oggetto di tipo diverso da quello dinamico.
 * 
 * @author svaponi
 */
public class YamlWrongTypeException extends CuseException {
	private static final long serialVersionUID = 1L;

	public YamlWrongTypeException() {
		super();
	}

	public YamlWrongTypeException(String message) {
		super(message);
	}
}