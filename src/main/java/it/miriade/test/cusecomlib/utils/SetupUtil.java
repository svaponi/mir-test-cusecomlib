package it.miriade.test.cusecomlib.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.exec.OS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import it.miriade.test.cusecomlib.CuseDefaultSpec;
import it.miriade.test.cusecomlib.CuseSetupConfiguration;
import it.miriade.test.cusecomlib.enums.Browser;

/**
 * Utilities per inizializzare la suite di test
 * 
 * @author svaponi
 */
public class SetupUtil implements CuseDefaultSpec {

	private static final Logger log = LoggerFactory.getLogger(SetupUtil.class);

	private static final String OS_NAME = System.getProperty("os.name", "?");

	private SetupUtil() {
		super();
	}

	/**
	 * Costruisce un CuseSetupConfiguration con i valori letti da un file di properties
	 * 
	 * @param propertyFilePath
	 */
	public static CuseSetupConfiguration buildCondigurationFromProperties(String propertyFilePath) {

		log.info("Building CuseSetupConfiguration from properties: {}", propertyFilePath);

		CuseSetupConfiguration config = new CuseSetupConfiguration();

		File propertyFile = new File(propertyFilePath);
		Assert.isTrue(propertyFile.exists(), "Invalid property file: " + propertyFilePath);
		Properties properties = new Properties();

		try (InputStream input = new FileInputStream(propertyFile)) {
			// load a properties file
			properties.load(input);

			if (properties.containsKey(TARGET_BROWSER))
				config.targetBrowser((String) properties.get(TARGET_BROWSER));
			if (properties.containsKey(TARGET_ENVIRONMENT))
				config.targetEnv((String) properties.get(TARGET_ENVIRONMENT));
			if (properties.containsKey(TARGET_TAGS))
				config.targetTags((String) properties.get(TARGET_TAGS));
			if (properties.containsKey(CLOSE_BROWSER))
				config.closeBrowser(Boolean.parseBoolean((String) properties.get(CLOSE_BROWSER)));
			if (properties.containsKey(YAML_SUPPORT_CLASSPATH_DIR))
				config.yamlClasspathDir((String) properties.get(YAML_SUPPORT_CLASSPATH_DIR));
			if (properties.containsKey(USE_PROPERTY_FILE))
				config.useProperties((String) properties.get(USE_PROPERTY_FILE));
			if (properties.containsKey(WHAT_TO_DO_ON_FAIL))
				config.onFail((String) properties.get(WHAT_TO_DO_ON_FAIL));

			if (properties.containsKey(WEBDRIVER_PATH_CHROME))
				config.pathChrome((String) properties.get(WEBDRIVER_PATH_CHROME));
			if (properties.containsKey(WEBDRIVER_PATH_EDGE))
				config.pathEdge((String) properties.get(WEBDRIVER_PATH_EDGE));
			if (properties.containsKey(WEBDRIVER_PATH_FIREFOX))
				config.pathFirefox((String) properties.get(WEBDRIVER_PATH_FIREFOX));
			if (properties.containsKey(WEBDRIVER_PATH_IE))
				config.pathIE((String) properties.get(WEBDRIVER_PATH_IE));

			if (properties.containsKey(WEBDRIVER_TIMEOUTS_FIND))
				config.timeoutFindElement(Double.parseDouble((String) properties.get(WEBDRIVER_TIMEOUTS_FIND)));
			if (properties.containsKey(WEBDRIVER_TIMEOUTS_LOAD))
				config.timeoutPageLoad(Double.parseDouble((String) properties.get(WEBDRIVER_TIMEOUTS_LOAD)));
			if (properties.containsKey(WEBDRIVER_TIMEOUTS_SCRIPT))
				config.timeoutScript(Double.parseDouble((String) properties.get(WEBDRIVER_TIMEOUTS_SCRIPT)));

			if (properties.containsKey(WEBDRIVER_WINDOW_POSITION))
				config.windowPos((String) properties.get(WEBDRIVER_WINDOW_POSITION));

		} catch (IOException e) {
			log.warn("Loading properties failed! {}", e.getMessage());
		}

		return config;
	}

	/**
	 * Setta delle System Properties in base a quanto letto da un file di properties
	 * 
	 * @param propertyFilePath
	 */
	public static void overrideSystemProperties(String propertyFilePath) {

		log.info("Override System Properties with properties from: {}", propertyFilePath);

		File propertyFile = new File(propertyFilePath);
		Assert.isTrue(propertyFile.exists(), "Invalid property file: " + propertyFilePath);
		Properties properties = new Properties();

		try (InputStream input = new FileInputStream(propertyFile)) {
			// load a properties file
			properties.load(input);
			properties.entrySet().forEach((entry) -> {
				log.debug("[override] {}={}", entry.getKey().toString(), entry.getValue().toString());
				System.setProperty(entry.getKey().toString(), entry.getValue().toString());
			});
		} catch (IOException e) {
			log.warn("Loading properties failed! {}", e.getMessage());
		}
	}

	/*
	 * Private
	 */

	/**
	 * Qui salviamo i path recuperati da {@link #inspectOsForDriverPath(Browser)}.
	 */
	private static final Map<Browser, String> cachedPaths = new HashMap<Browser, String>();

	/**
	 * Nomi degli eseguibili dei vari driver usati da {@link #inspectOsForDriverPath(Browser)}.
	 */
	private static final Map<Browser, String> driverExeNames = new HashMap<Browser, String>();

	static {
		driverExeNames.put(Browser.CHROME, "chromedriver");
		driverExeNames.put(Browser.FIREFOX, "geckodriver");
		driverExeNames.put(Browser.IE, "IEDriverServer.exe");
		driverExeNames.put(Browser.EDGE, "EdgeDriverServer.exe");
		// driverExeNames.put(Browser.SAFARI, "SafariDriver.safariextz");
	}

	/**
	 * Cerca di estrarre il path del driver sulla macchina locale, interrogando dal sistema operativo
	 * 
	 * @param brw
	 *            browser per il quale cercare il driver
	 * @return
	 */
	public static String inspectOsForDriverPath(Browser brw) {

		// i path recuperatri vengono memorizzati
		if (cachedPaths.containsKey(brw))
			return cachedPaths.get(brw);

		String path = null;

		if (!driverExeNames.containsKey(brw)) {

			log.warn("Inspect OS for driver path not implemented for " + brw);

		} else if (OS.isFamilyUnix()) {
			/*
			 * Implementazione per UNIX
			 */
			path = runShellCommand("which " + driverExeNames.get(Browser.CHROME));

		} else if (OS.isFamilyWindows()) {
			/*
			 * Implementazione per Windows
			 */
			log.warn("Inspect OS for driver path not implemented in " + OS_NAME);

		} else if (OS.isFamilyMac()) {
			/*
			 * Implementazione per Windows
			 */
			log.warn("Inspect OS for driver path not implemented in " + OS_NAME);

		} else {
			/*
			 * Unknow OS
			 */
			log.warn("Inspect OS for driver path not implemented in " + OS_NAME);
		}

		cachedPaths.put(brw, path);
		return path;
	}

	/**
	 * Tira un comando sulla shell del sistema operativo usando Java {@link Runtime}.
	 * 
	 * @param command
	 * @return
	 */
	public static String runShellCommand(String command) {
		StringBuilder buf = new StringBuilder();
		BufferedReader reader = null;
		try {

			Process p = Runtime.getRuntime().exec(command);
			p.waitFor();
			reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			reader.lines().forEach(line -> buf.append(line));
			log.trace("Run against OS `{}` => `{}`", command, buf.toString());

		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			try {
				reader.close();
				// close() chiude anche InputStreamReader contenuto in esso
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}
		return buf.toString();
	}

}
