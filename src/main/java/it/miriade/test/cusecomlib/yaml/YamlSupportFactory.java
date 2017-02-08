package it.miriade.test.cusecomlib.yaml;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import it.miriade.test.cusecomlib.CuseDefaultSpec;
import it.miriade.test.cusecomlib.CuseSetupConfiguration;
import it.miriade.test.cusecomlib.cucumber.pageobjs.PageObject;
import it.miriade.test.cusecomlib.utils.ReflectionUtil;

/**
 * Factory per creare oggetti di tipo {@link YamlSupport}.
 * Inoltre implementa una cache che evita di costruire più volte lo stesso oggetto.
 * 
 * @author svaponi
 */
public class YamlSupportFactory {

	private final Logger log = LoggerFactory.getLogger(getClass());
	private final Map<String, YamlSupport> yamls = new HashMap<>();
	private static final String YML_EXT = ".yml";

	private CuseSetupConfiguration config;

	public YamlSupportFactory(CuseSetupConfiguration config) {
		super();
		this.config = config;
	}

	/**
	 * Ritorna lo {@link YamlSupport} comune
	 * 
	 * @see CuseDefaultSpec#YAML_SUPPORT_COMMON_FILENAME
	 * @return
	 */
	public YamlSupport getCommonYaml() {
		return build(CuseDefaultSpec.YAML_SUPPORT_COMMON_FILENAME);
	}

	/**
	 * Ritorna lo {@link YamlSupport} associato al nome. Il path dove cercare è preso dalla
	 * {@link CuseSetupConfiguration}.
	 * 
	 * @see CuseSetupConfiguration#yamlClasspathDir()
	 * @param yamlName
	 *            nome dello YAML (estensione è opzionale)
	 * @return {@link YamlSupport}
	 */
	public YamlSupport build(String yamlName) {
		String yamlPath = path(yamlName);
		if (yamls.containsKey(yamlPath))
			return yamls.get(yamlPath);
		try {
			log.debug("Initializing {}({})", YamlSupport.class.getSimpleName(), yamlPath);
			YamlSupport yaml = new YamlSupport(yamlPath);
			yamls.put(yamlPath, yaml);
			return yaml;
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed to build YamlSupport: " + e.getMessage());
		}
	}

	/**
	 * Ritorna lo {@link YamlSupport}. Lo YAML si deve chiamare come la classe (nome completo con anche il package +
	 * estenzione .yml)
	 * 
	 * @param caller
	 *            classe che deve usare lo YAML
	 * @return {@link YamlSupport}
	 */
	public YamlSupport build(Class<?> caller) {
		return build(getYamlNameByClass(caller));
	}

	/**
	 * Mi dice se ho già uno {@link YamlSupport} cache-ato.
	 * 
	 * @param yamlName
	 *            nome dello YAML (estensione è opzionale)
	 * @return booleano
	 */
	public boolean has(String yamlName) {
		String yamlPath = path(yamlName);
		return yamls.containsKey(yamlPath);
	}

	/*
	 * Costruisce il path completo del file YAML partendo dal nome in input
	 */
	private String path(String yamlName) {
		Assert.notNull(config, "config is null");
		String tmp = config.yamlClasspathDir();
		String yamlClasspathDir = tmp.isEmpty() || tmp.endsWith("/") ? tmp : tmp.concat("/");
		String yamlPath = yamlClasspathDir + yamlName + (yamlName.endsWith(YML_EXT) ? "" : YML_EXT);
		return yamlPath;
	}

	/*
	 * Autodetect del chiamante
	 */

	private final ReflectionUtil reflectionUtil = new ReflectionUtil();

	/**
	 * Ritorna lo YamlSupport associato al chiamante (autodetect del chiamante).
	 * La factory riesce a vedere chi invoca il metodo poi, applicando la naming
	 * convention implementata in {@link #getYamlNameByClass(Class)}, recupera il path dello YAML ad esso associato.
	 * 
	 * @return
	 */
	public YamlSupport build() {
		Class<?> caller = reflectionUtil.getCaller(PageObject.class);
		log.trace("Checking YamlSupport for " + caller);
		return build(caller);
	}

	/**
	 * Ritorna il nome dello YAML associato al PageObject (nome del file senza estensione). Qui definisco la naming
	 * convention.
	 * 
	 * @param caller
	 * @return
	 */
	public static String getYamlNameByClass(Class<?> caller) {
		return caller.getSimpleName();
	}

}