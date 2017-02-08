package it.miriade.test.cusecomlib.excep;

/**
 * Indica che è la key immessa non corrisponde ad un persorso valido nello YAML file.
 * 
 * @author svaponi
 */
public class YamlInvalidKeyException extends CuseException {
	private static final long serialVersionUID = 1L;

	public YamlInvalidKeyException() {
		super();
	}

	public YamlInvalidKeyException(String message) {
		super(message);
	}
}
