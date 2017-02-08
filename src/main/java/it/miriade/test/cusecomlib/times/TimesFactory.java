package it.miriade.test.cusecomlib.times;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import it.miriade.test.cusecomlib.CuseDefaultSpec;
import it.miriade.test.cusecomlib.yaml.YamlSupport;

/**
 * Factory per creare oggetti di tipo {@link Times}.
 * 
 * @author svaponi
 */
public class TimesFactory {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private YamlSupport _yaml;

	public TimesFactory() {
		super();
	}

	/**
	 * @param yaml
	 *            {@link YamlSupport} per il file che contiene i tempi di attesa
	 */
	public TimesFactory(YamlSupport yaml) {
		super();
		this._yaml = yaml;
	}

	// YamlSupport
	// ========================================================================

	private YamlSupport yaml() throws UnsupportedOperationException {
		if (_yaml == null)
			throw new UnsupportedOperationException(CuseDefaultSpec.YAML_UNSUPPORTED_MESSAGE);
		return _yaml;
	}

	// ========================================================================

	/**
	 * Metodo legge gli argomenti e converte in modo intelligente e ritorna i tempi di attesa in un oggetto
	 * {@link Times}.
	 * 
	 * @param yaml
	 *            {@link YamlSupport} da cui recuperare i tempi
	 * @param args
	 *            tempo di attesa oppure chiave per leggere la configurazione e recuperare il tempo di attesa
	 * @return
	 * @throws UnsupportedOperationException
	 *             se manca lo YAML
	 */
	public static Times build(YamlSupport yaml, Object... args) throws UnsupportedOperationException {
		return new TimesFactory(yaml).build(args);
	}

	/**
	 * Metodo legge gli argomenti e converte in modo intelligente e ritorna i tempi di attesa in un oggetto
	 * {@link Times}.
	 * 
	 * @param args
	 *            tempo di attesa oppure chiave per leggere la configurazione e recuperare il tempo di attesa
	 * @return
	 * @throws UnsupportedOperationException
	 *             se manca lo YAML
	 */
	public Times build(Object... args) throws UnsupportedOperationException {

		if (args.length == 0)
			throw new IllegalArgumentException("Mancano i tempi di attesa");

		if (args[0] instanceof Times)
			return (Times) args[0];

		if (args[0] instanceof String) {

			String key = ((String) args[0]).toLowerCase();
			Map<String, ?> timesMap = yaml().getMap("?.?", CuseDefaultSpec.YAML_SUPPORT_TIMES_PREFIX, key);
			if (timesMap == null)
				throw new IllegalArgumentException("Tempi di attesa invalidi => manca la chiave \"" + key + "\" in common.yml");

			try {

				return new Times(timesMap);

			} catch (Exception e) {
				log.debug(e.getMessage(), e);
				throw new IllegalArgumentException("Tempi di attesa invalidi => controllare i valori della chiave \"" + key + "\" in common.yml");
			}

		} else {

			try {

				if (args.length > 2)
					return new Times((Number) args[0], (Number) args[1], (Number) args[2]);

				else if (args.length > 1)
					return new Times((Number) args[0], (Number) args[1]);

				else
					return new Times((Number) args[0]);

			} catch (Exception e) {
				log.debug(e.getMessage(), e);
				throw new IllegalArgumentException("Tempi di attesa invalidi => " + StringUtils.arrayToDelimitedString(args, ","));
			}
		}
	}
}