package it.miriade.test.cusecomlib.yaml;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;

import it.miriade.test.cusecomlib.excep.YamlInvalidKeyException;
import it.miriade.test.cusecomlib.excep.YamlWrongTypeException;

/**
 * Utility per leggere i file YAML. Dispone di getter compatibili con la <i>Property File Syntax</i>, ovvero la
 * notazione dei file di properties, dove il '.' indica il livello successivo/annidato.<br/>
 * Esempio:
 * 
 * <pre>
 * YamlHandler yaml = new YamlHandler("path/to/file.yml");
 * Map<String, ?> data = yaml.getMap("data");
 * List<?> users = yaml.getList("data.users");
 * String name = yaml.getString("data.users[0].name");
 * int age = yaml.getInteger("data.users[0].age");
 * </pre>
 * 
 * @see <a href="http://yaml.org/spec/">http://yaml.org/spec/</a>
 * @author svaponi
 */
@SuppressWarnings("unchecked")
public class YamlSupport {

	private final Logger log = LoggerFactory.getLogger(getClass());
	private String yamlPath;
	private String yamlName;
	private boolean throwOnInvalidKey;
	private Map<String, Object> data;

	/**
	 * <strong>ATTENZIONE</strong>: il costruttore è definito a livello di accesso package per forzare l'utilizzo di
	 * {@link YamlSupportFactory} per l'inizializzazione.
	 * 
	 * @see YamlSupportFactory
	 * @param yamlFilePath
	 *            path del file YAML
	 */
	YamlSupport(String yamlFilePath) {
		this(yamlFilePath, true);
	}

	/**
	 * @param yamlFilePath
	 *            path del file YAML
	 * @param throwOnInvalidKey
	 *            Influisce sul comportamento del metodo {@link #get(String)} (e analoghi) in caso di key invalida o
	 *            inesistente:
	 *            <ul>
	 *            <li>se FALSE ritorna null</li>
	 *            <li>se TRUE (comportamento di default) viene sollevata una
	 *            {@link YamlInvalidKeyException}</li>
	 *            </ul>
	 */
	YamlSupport(String yamlFilePath, boolean throwOnInvalidKey) {
		Assert.hasText(yamlFilePath, "Empty path");
		this.throwOnInvalidKey = throwOnInvalidKey;
		this.yamlPath = yamlFilePath;
		log.trace("Loading YAML file: {}", yamlFilePath);
		try {
			URL resource = YamlSupport.class.getClassLoader().getResource(yamlFilePath);
			if (resource == null)
				throw new IllegalArgumentException("Missing " + yamlFilePath);
			File yamlFile = new File(resource.getPath());
			this.yamlName = StringUtils.stripFilenameExtension(yamlFile.getName());
			data = (Map<String, Object>) new Yaml().load(new FileInputStream(yamlFile));
		} catch (Exception e) {
			throw new IllegalArgumentException("Impossibile leggere YAML file [" + yamlFilePath + "]: " + e.getMessage() + "");
		}
	}

	/**
	 * @return
	 * 		path assoluto al file YAML
	 */
	public String getYamlPath() {
		return yamlPath;
	}

	/**
	 * @return
	 * 		nome dello YAML (nome del file senza estensione)
	 */
	public String getYamlName() {
		return yamlName;
	}

	/**
	 * Indica il comportamento di {@link YamlSupport#get(String)} (e degli altri getters) in caso di key invalida.
	 * 
	 * @return
	 * 		false: ritorna NULL se la key è invalida o inesistente <br/>
	 *         true: solleva InvalidKeyException se la key è invalida o inesistente
	 */
	public boolean isThrowOnInvalidKey() {
		return throwOnInvalidKey;
	}

	/*
	 * Metodi per navigare la mappa usando la notazione delle property
	 */

	/**
	 * Naviga l'oggetto Map secondo la notazione delle property, dove il '.'
	 * indica il livello successivo.
	 * 
	 * @param key
	 * @return value
	 * @throws YamlInvalidKeyException
	 *             se la key è invalida o inesistente
	 */
	public Object get(String key) throws YamlInvalidKeyException {
		try {
			Object result = recursiveGet(key, data);
			log.trace("[{}] {} = {}", yamlName, key, result);
			return result;
		} catch (YamlInvalidKeyException e) {
			if (throwOnInvalidKey)
				throw new YamlInvalidKeyException("Invalid key expression: " + key);
			else
				return null;
		}
	}

	/**
	 * Come {@link #get(String)} però fa un cast esplicito al {@link String}
	 * 
	 * @param key
	 * @return value
	 * @throws YamlInvalidKeyException
	 *             se la key è invalida o inesistente
	 * @throws YamlWrongTypeException
	 *             se il tipo dinamico dell'oggetto di ritorno è diverso da quello in input
	 */
	public String getString(String key) throws YamlInvalidKeyException, YamlWrongTypeException {
		return genericGet(String.class, key);
	}

	/**
	 * Come {@link #get(String)} però fa un cast esplicito al {@link Integer}
	 * 
	 * @param key
	 * @return value
	 * @throws YamlInvalidKeyException
	 *             se la key è invalida o inesistente
	 * @throws YamlWrongTypeException
	 *             se il tipo dinamico dell'oggetto di ritorno è diverso da quello in input
	 */
	public Integer getInteger(String key) throws YamlInvalidKeyException, YamlWrongTypeException {
		Number number = genericGet(Number.class, key);
		return number == null ? null : number.intValue();
	}

	/**
	 * Come {@link #get(String)} però fa un cast esplicito al {@link Double}
	 * 
	 * @param key
	 * @return value
	 * @throws YamlInvalidKeyException
	 *             se la key è invalida o inesistente
	 * @throws YamlWrongTypeException
	 *             se il tipo dinamico dell'oggetto di ritorno è diverso da quello in input
	 */
	public Double getDouble(String key) throws YamlInvalidKeyException, YamlWrongTypeException {
		Number number = genericGet(Number.class, key);
		return number == null ? null : number.doubleValue();
	}

	/**
	 * Come {@link #get(String)} però fa un cast esplicito al {@link Boolean}
	 * 
	 * @param key
	 * @return value
	 * @throws YamlInvalidKeyException
	 *             se la key è invalida o inesistente
	 * @throws YamlWrongTypeException
	 *             se il tipo dinamico dell'oggetto di ritorno è diverso da quello in input
	 */
	public Boolean getBoolean(String key) throws YamlInvalidKeyException, YamlWrongTypeException {
		return genericGet(Boolean.class, key);
	}

	/**
	 * Come {@link #get(String)} però fa un cast esplicito al {@link List}
	 * 
	 * @param key
	 * @return value
	 * @throws YamlInvalidKeyException
	 *             se la key è invalida o inesistente
	 * @throws YamlWrongTypeException
	 *             se il tipo dinamico dell'oggetto di ritorno è diverso da quello in input
	 */
	public List<?> getList(String key) throws YamlInvalidKeyException, YamlWrongTypeException {
		return genericGet(List.class, key);
	}

	/**
	 * Come {@link #get(String)} però fa un cast esplicito al {@link Map}
	 * 
	 * @param key
	 * @return value
	 * @throws YamlInvalidKeyException
	 *             se la key è invalida o inesistente
	 * @throws YamlWrongTypeException
	 *             se il tipo dinamico dell'oggetto di ritorno è diverso da quello in input
	 */
	public Map<String, Object> getMap(String key) throws YamlInvalidKeyException, YamlWrongTypeException {
		return genericGet(Map.class, key);
	}

	/*
	 * Innietta gli argomenti args nella keyTemplate per comporre la chiave da
	 * utilizzare, es. keyTemplate = "?.users.?.userid", args = {"TEST",
	 * "PIPPO"} ==> key = "TEST.users.PIPPO.userid"
	 */

	/**
	 * Come {@link #get(String)} però da la possibilità di inniettare argomenti nella key per comporre
	 * la chiave da utilizzare, es. keyTemplate = "?.users.?.userid", args = {"TEST",
	 * "PIPPO"} ==> key = "TEST.users.PIPPO.userid"
	 * 
	 * @param keyTemplate
	 *            template della key con '?' come placeholders
	 * @param args
	 *            parametri da inniettare
	 * @return value
	 * @throws YamlInvalidKeyException
	 *             se la key è invalida o inesistente
	 */
	public Object get(String keyTemplate, Object... args) throws YamlInvalidKeyException {
		return get(buildKey(keyTemplate, args));
	}

	/**
	 * Come {@link #get(String, Object...)} però fa un cast esplicito al {@link String}
	 * 
	 * @param keyTemplate
	 * @param args
	 * @return value
	 * @throws YamlInvalidKeyException
	 *             se la key è invalida o inesistente
	 * @throws YamlWrongTypeException
	 *             se il tipo dinamico dell'oggetto di ritorno è diverso da quello in input
	 */
	public String getString(String keyTemplate, Object... args) throws YamlInvalidKeyException, YamlWrongTypeException {
		return getString(buildKey(keyTemplate, args));
	}

	/**
	 * Come {@link #get(String, Object...)} però fa un cast esplicito al {@link Integer}
	 * 
	 * @param keyTemplate
	 * @param args
	 * @return value
	 * @throws YamlInvalidKeyException
	 *             se la key è invalida o inesistente
	 * @throws YamlWrongTypeException
	 *             se il tipo dinamico dell'oggetto di ritorno è diverso da quello in input
	 */
	public Integer getInteger(String keyTemplate, Object... args) throws YamlInvalidKeyException, YamlWrongTypeException {
		return getInteger(buildKey(keyTemplate, args));
	}

	/**
	 * Come {@link #get(String, Object...)} però fa un cast esplicito al {@link Double}
	 * 
	 * @param keyTemplate
	 * @param args
	 * @return value
	 * @throws YamlInvalidKeyException
	 *             se la key è invalida o inesistente
	 * @throws YamlWrongTypeException
	 *             se il tipo dinamico dell'oggetto di ritorno è diverso da quello in input
	 */
	public Double getDouble(String keyTemplate, Object... args) throws YamlInvalidKeyException, YamlWrongTypeException {
		return getDouble(buildKey(keyTemplate, args));
	}

	/**
	 * Come {@link #get(String, Object...)} però fa un cast esplicito al {@link Boolean}
	 * 
	 * @param keyTemplate
	 * @param args
	 * @return value
	 * @throws YamlInvalidKeyException
	 *             se la key è invalida o inesistente
	 * @throws YamlWrongTypeException
	 *             se il tipo dinamico dell'oggetto di ritorno è diverso da quello in input
	 */
	public Boolean getBoolean(String keyTemplate, Object... args) throws YamlInvalidKeyException, YamlWrongTypeException {
		return getBoolean(buildKey(keyTemplate, args));
	}

	/**
	 * Come {@link #get(String, Object...)} però fa un cast esplicito al {@link List}
	 * 
	 * @param keyTemplate
	 * @param args
	 * @return value
	 * @throws YamlInvalidKeyException
	 *             se la key è invalida o inesistente
	 * @throws YamlWrongTypeException
	 *             se il tipo dinamico dell'oggetto di ritorno è diverso da quello in input
	 */
	public List<?> getList(String keyTemplate, Object... args) throws YamlInvalidKeyException, YamlWrongTypeException {
		return getList(buildKey(keyTemplate, args));
	}

	/**
	 * Come {@link #get(String, Object...)} però fa un cast esplicito al {@link Map}
	 * 
	 * @param keyTemplate
	 * @param args
	 * @return value
	 * @throws YamlInvalidKeyException
	 *             se la key è invalida o inesistente
	 * @throws YamlWrongTypeException
	 *             se il tipo dinamico dell'oggetto di ritorno è diverso da quello in input
	 */
	public Map<String, ?> getMap(String keyTemplate, Object... args) throws YamlInvalidKeyException, YamlWrongTypeException {
		return getMap(buildKey(keyTemplate, args));
	}

	/*
	 * PRIVATE
	 */

	/**
	 * Utility per castare gli oggetti ritornati dalla {@link #get(String)}
	 * 
	 * @param clazz
	 * @param key
	 * @return value
	 * @throws YamlWrongTypeException
	 *             se il tipo dinamico dell'oggetto di ritorno è diverso da quello in input
	 */
	private <T> T genericGet(Class<T> clazz, String key) throws YamlWrongTypeException {
		Object tmp = get(key);
		if (tmp != null && !clazz.isAssignableFrom(tmp.getClass()))
			throw new YamlWrongTypeException("Actual type is " + tmp.getClass());
		return (T) tmp;
	}

	/**
	 * Costruisce la key inniettando i paramentri in input
	 * 
	 * @param keyTemplate
	 * @param args
	 * @return key
	 */
	private String buildKey(String keyTemplate, Object... args) {
		for (Object arg : args)
			keyTemplate = keyTemplate.replaceFirst("\\?", arg == null ? "" : arg.toString());
		return keyTemplate;
	}

	/**
	 * Metodo ricorsivo per estrarre gli items dalla mappa usando
	 * 
	 * @param key
	 * @param data
	 * @return value
	 * @throws YamlInvalidKeyException
	 *             se la key è invalida o inesistente
	 */
	private Object recursiveGet(String key, Map<String, Object> data) throws YamlInvalidKeyException {
		if (key == null || key.isEmpty())
			return "";

		// log.trace("{} = {}", key, data);

		String[] keys = key.split("\\.");

		/*
		 * Controllo se la prima chiave (prima del primo punto) identifica un
		 * array, in tal caso prendo l'elemento indicato dall'indice tra le [].
		 */
		ArrayResult res = detectArray(keys[0]);
		Object tmp;
		if (res != null) {
			// log.trace("Found array expression: {}", keys[0]);
			tmp = data.get(res.key);
			if (tmp instanceof List<?>)
				try {
					tmp = ((List<?>) tmp).get(res.index);
					// log.trace("{}[{}] = {}", res.key, res.index, tmp);
				} catch (IndexOutOfBoundsException e) {
					throw new YamlInvalidKeyException();
				}
			else
				throw new YamlInvalidKeyException();
		} else
			tmp = data.get(keys[0]);

		/*
		 * Controllo se ho un oggetto mappa e se ho ancora livelli da scendere
		 * (identificati dal '.')
		 */
		if (tmp instanceof Map<?, ?> && key.contains("."))
			return recursiveGet(key.substring(keys[0].length() + 1), (Map<String, Object>) tmp);
		else if (tmp != null)
			return tmp;
		else
			throw new YamlInvalidKeyException();
	}

	private static final Pattern isArray = Pattern.compile("(.*)\\[(\\d*)\\](.*)");

	private ArrayResult detectArray(String key) {
		Matcher m = isArray.matcher(key);
		if (m.find())
			return new ArrayResult(m.group(1), Integer.parseInt(m.group(2)));
		return null;
	}

	/**
	 * Classe che incapsula il risultato temporaneo che riscontro quando incontro un array lungo il percorso tracciato
	 * dalla key nel property file.
	 * 
	 * @author svaponi
	 */
	private class ArrayResult {
		String key;
		int index;

		public ArrayResult(String key, int index) {
			super();
			this.index = index;
			this.key = key;
		}
	}
}
