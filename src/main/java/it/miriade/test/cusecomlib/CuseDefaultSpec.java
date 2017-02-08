package it.miriade.test.cusecomlib;

import java.util.regex.Pattern;

import it.miriade.test.cusecomlib.enums.Browser;
import it.miriade.test.cusecomlib.hooks.HtmlHook;
import it.miriade.test.cusecomlib.selenium.SeleniumWebDriverWrapper;
import it.miriade.test.cusecomlib.times.Times;
import it.miriade.test.cusecomlib.utils.SetupUtil;

/**
 * Contenitore di variabili e specifiche di default.<br/>
 * Contiene anche le chiavi delle system property utilizzate per parametrizzare l'esecuzione dei test.
 * 
 * @author svaponi
 */
public interface CuseDefaultSpec {

	/**
	 * Classe di configurazione di Spring.
	 */
	public static final Class<?> CONTEXT_CONFIG_CLASS = CuseSpringConfiguration.class;

	/**
	 * Nome del property file da aggiungere nel classpath da chi utilizza la libreria.
	 */
	public static final String CUSE_PROPERTY_FILE = "cuse.properties";

	/*
	 * System Properties di controllo per invocare i test
	 */

	/**
	 * Proprietà che indica il browser da utilizzare per i test.
	 * 
	 * @see Browser
	 */
	public static final String TARGET_BROWSER = "target.browser";

	/**
	 * Proprietà che indica contro quale ambiente eseguire i test.
	 */
	public static final String TARGET_ENVIRONMENT = "target.env";

	/**
	 * Proprietà che indica quali scenari eseguire.
	 */
	public static final String TARGET_TAGS = "target.tags";

	/**
	 * Proprietà che permette di inibire la chiusura del browser. Di default è TRUE, ovvero il browser viene chiuso
	 * assieme al proprio {@link SeleniumWebDriverWrapper} (generalmente quando viene chiuso il contesto di Spring alla
	 * fine dei test). Se settata a FALSE lascia il browser aperto alla fine dei test.
	 */
	public static final String CLOSE_BROWSER = "close.browser";

	/**
	 * Proprietà che indica la directory dove verranno salvati gli screenshots. Se omessa sono salvati in:
	 * 
	 * <pre>
	 * screenshots/
	 * </pre>
	 */
	public static final String SCREENSHOTS_DIR = "screenshots.dir";

	/**
	 * Proprietà che indica il comportamento in caso di errore dei test. Se omessa il default è "do-nothing".<br/>
	 * <strong>SOLO PER DEVELOPER</strong><br/>
	 * Usato durante lo sviluppo dei test, permette di bloccare l'esecuzione in
	 * caso di errore/eccezione e dare modo allo sviluppatore di interagire tramite STDIN della console e, ad esempio,
	 * testare le espressioni degli hooks.
	 */
	public static final String WHAT_TO_DO_ON_FAIL = "on.fail";

	/**
	 * Proprietà che indica il file esterno di properties da utilizzare per l'esecuzione corrente dei test.
	 */
	public static final String USE_PROPERTY_FILE = "use.properties";

	/*
	 * System Properties legacy di Selenium
	 */

	/**
	 * Nome della System-property che localizza il driver per Chrome (chromedriver)
	 */
	public static final String WEBDRIVER_PATH_CHROME = "webdriver.chrome.driver";

	/**
	 * Nome della System-property che localizza il driver per Firefox (geckodriver)
	 * <br/>
	 * Vedi <a href="https://github.com/mozilla/geckodriver">https://github.com/mozilla/geckodriver</a>
	 */
	public static final String WEBDRIVER_PATH_FIREFOX = "webdriver.gecko.driver";

	/**
	 * Nome della System-property che localizza il driver per IE (IEDriverServer.exe)
	 */
	public static final String WEBDRIVER_PATH_IE = "webdriver.ie.driver";

	/**
	 * Nome della System-property che localizza il driver per Edge (MicrosoftWebDriver.exe)
	 */
	public static final String WEBDRIVER_PATH_EDGE = "webdriver.edge.driver";

	/*
	 * System Properties custom per inizializzare il WebDriver
	 */

	/**
	 * Proprietà che contiene il timeout {@link #WEBDRIVER_TIMEOUTS_FIND} in SECONDI. Il default è un secondo. Per info
	 * vedi <a href=
	 * "https://seleniumhq.github.io/selenium/docs/api/java/org/openqa/selenium/WebDriver.Timeouts.html">documentazione</a>
	 * online.
	 */
	public static final String WEBDRIVER_TIMEOUTS_FIND = "webdriver.timeouts.find";

	/**
	 * Proprietà che contiene il timeout {@link #WEBDRIVER_TIMEOUTS_LOAD} in SECONDI. Il default è 60 secondi. Per info
	 * vedi <a href=
	 * "https://seleniumhq.github.io/selenium/docs/api/java/org/openqa/selenium/WebDriver.Timeouts.html">documentazione</a>
	 * online.
	 */
	public static final String WEBDRIVER_TIMEOUTS_LOAD = "webdriver.timeouts.load";

	/**
	 * Proprietà che contiene il timeout {@link #WEBDRIVER_TIMEOUTS_SCRIPT} in SECONDI. Il default è 60 secondi. Per
	 * info
	 * vedi <a href=
	 * "https://seleniumhq.github.io/selenium/docs/api/java/org/openqa/selenium/WebDriver.Timeouts.html">documentazione</a>
	 * online.
	 */
	public static final String WEBDRIVER_TIMEOUTS_SCRIPT = "webdriver.timeouts.script";

	/**
	 * Proprietà per settare la posizione del browser nello schermo. Se "maximize" allora massimizza la finestra del
	 * browser.
	 */
	public static final String WEBDRIVER_WINDOW_POSITION = "webdriver.window.position";

	/*
	 * Parametri e specifiche per lo YAML Support
	 */

	/**
	 * Proprietà per cambiare la directory base dove mettere i file YAML.
	 * <strong>ATTENZIONE</strong>: deve essere nel classpath, dunque dentro una delle cartelle root es.
	 * 
	 * <pre>
	 * src/main/java
	 * src/main/resources
	 * src/test/java
	 * src/test/resources
	 * </pre>
	 */
	public String YAML_SUPPORT_CLASSPATH_DIR = "yaml.classpath.dir";

	/**
	 * Nome standard del file YAML comune a tutte le utility e condiviso tra gli ambienti. Vedi file
	 * {@value #YAML_SUPPORT_COMMON_FILENAME}.yml nella cartella localizzata dalla property
	 * {@value #YAML_SUPPORT_CLASSPATH_DIR}
	 */
	public String YAML_SUPPORT_COMMON_FILENAME = "common";

	/**
	 * Messaggio della eccezione in caso lo YAML non sia supportato
	 */
	public String YAML_UNSUPPORTED_MESSAGE = "Unsupported YAML";

	/**
	 * Parent key per gli {@link HtmlHook} mappati in {@value #YAML_SUPPORT_COMMON_FILENAME} YAML file
	 */
	public String YAML_SUPPORT_HOOKS_PREFIX = "hooks";

	/**
	 * Parent key per i {@link Pattern} delle regexp mappate in {@value #YAML_SUPPORT_COMMON_FILENAME} YAML
	 * file
	 */
	public String YAML_SUPPORT_REGEXP_PREFIX = "regexp";

	/**
	 * Parent key per i {@link Times} mappati in {@value #YAML_SUPPORT_COMMON_FILENAME} YAML file
	 */
	public String YAML_SUPPORT_TIMES_PREFIX = "times";

	/*
	 * - - - - - - - - - - - - - - - - - - - - - - - - -
	 * Backdoors (SOLO PER DEVELOPER)
	 * - - - - - - - - - - - - - - - - - - - - - - - - -
	 */

	/**
	 * Proprietà che indica se delegare la ricerca del path del webdriver al SO.<br/>
	 * In caso il path del webdriver sia nullo, e' possibile delegare la ricerca del path locale al sistema operativo.
	 * Per abilitare la funzionalità aggiungere la System Property {@value #INSPECT_OS}. Esempio:
	 * 
	 * <pre>
	 * mvn test -Dinspect.os
	 * </pre>
	 * 
	 * <strong>ATTENZIONE</strong>: la ricerca funziona solo se i test sonolanciati dalla stessa shell che ha in pancia
	 * la $PATH variable del sistema operativo, dunque se lancio da Eclipse non vede il path, mentre se lancio da riga
	 * di comando con `mvn test` invece si.
	 * 
	 * @see SetupUtil#inspectOsForDriverPath(Browser)
	 */
	public String INSPECT_OS = "inspect.os";

	/**
	 * <strong>SOLO PER DEVELOPER</strong><br/>
	 * Proprietà che indica se abilitare i test di integrazione, ovvero quei test che dipendono dall'ambiente.<br/>
	 * Durante gli sviluppi della common-lib è plausibile voler invocare dei test che utilizzino un webdriver locale o
	 * remoto per verificare le funzionalità della libreria. Questi test sono considerati di integrazione perchè
	 * dipendono dall'ambiente, e perciò sono normalmente disabilitati. Per abilitare i test di integrazione sulla
	 * common-lib aggiungere <code>run.integration.tests</code> al comando di invocazione degli unit test sulla
	 * libreria.
	 * 
	 * <pre>
	 * mvn test -Drun.integration.tests
	 * </pre>
	 */
	public String RUN_INTEGRATION_TESTS = "run.integration.tests";

}
