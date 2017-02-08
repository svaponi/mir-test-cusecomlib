package it.miriade.test.cusecomlib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import it.miriade.test.cusecomlib.cucumber.CucumberSpec;
import it.miriade.test.cusecomlib.enums.Browser;
import it.miriade.test.cusecomlib.utils.SetupUtil;

/**
 * Si occupa di leggere i parametri di configurazione dalle varie sorgenti. Per ordine di priorità vedi README.md.
 * 
 * @author svaponi
 */
public class CuseSetupConfiguration implements CuseDefaultSpec, InitializingBean {

	private final Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * browser di default da usare nei test
	 * <blockquote>
	 * target.browser=chrome
	 * </blockquote>
	 */
	@Value("${" + TARGET_BROWSER + ":chrome}")
	private String targetBrowser;

	/**
	 * Ambiente di default
	 * <blockquote>
	 * target.env=test
	 * </blockquote>
	 */
	@Value("${" + TARGET_ENVIRONMENT + ":test}")
	private String targetEnv;

	@Value("${" + TARGET_TAGS + ":}")
	private String targetTags;

	@Value("${" + WEBDRIVER_PATH_CHROME + "}")
	private String pathChrome;

	@Value("${" + WEBDRIVER_PATH_FIREFOX + "}")
	private String pathFirefox;

	@Value("${" + WEBDRIVER_PATH_IE + "}")
	private String pathIE;

	@Value("${" + WEBDRIVER_PATH_EDGE + "}")
	private String pathEdge;

	/**
	 * Specifies the amount of time the driver should wait when searching for an element if it is not immediately
	 * present
	 * <blockquote>
	 * webdriver.timeouts.find=1
	 * </blockquote>
	 */
	@Value("${" + WEBDRIVER_TIMEOUTS_FIND + ":1.0}")
	private double findElementTimeout;

	/**
	 * Sets the amount of time to wait for a page load to complete before throwing an error
	 * <blockquote>
	 * webdriver.timeouts.load=120
	 * </blockquote>
	 */
	@Value("${" + WEBDRIVER_TIMEOUTS_LOAD + ":60.0}")
	private double pageLoadTimeout;

	/**
	 * Sets the amount of time to wait for an asynchronous script to finish execution before throwing an error
	 * <blockquote>
	 * webdriver.timeouts.script=120
	 * </blockquote>
	 */
	@Value("${" + WEBDRIVER_TIMEOUTS_SCRIPT + ":60.0}")
	private double scriptTimeout;

	/**
	 * Configurazione finestra del browser
	 * <blockquote>
	 * webdriver.window.position=maximize
	 * </blockquote>
	 */
	@Value("${" + WEBDRIVER_WINDOW_POSITION + ":maximize}")
	private String windowPos;

	@Value("${" + CLOSE_BROWSER + ":true}")
	private boolean closeBrowser;

	@Value("${" + YAML_SUPPORT_CLASSPATH_DIR + ":cucumber/miriade/yaml/}")
	private String yamlClasspathDir;

	@Value("${" + SCREENSHOTS_DIR + ":screenshots/}")
	private String screenshotDir;

	@Value("${" + WHAT_TO_DO_ON_FAIL + ":do-nothing}")
	private String onFail;

	@Value("${" + USE_PROPERTY_FILE + ":}")
	private String useProperties;

	// Cucumber options - - - - - - - - - - - - - - - - - - - -

	@Value("${" + CucumberSpec.OVERRIDE_GLUE + ":" + CucumberSpec.GLUE + "}")
	private String cucumberGlueClasspth;

	@Value("${" + CucumberSpec.OVERRIDE_FEATURES + ":" + CucumberSpec.FEATURES + "}")
	private String cucumberFeaturesPath;

	/**
	 * Costruttore senza parametri assegna i valori di default. Assegna gli stessi valori inniettati dallo Spring
	 * Context.
	 */
	public CuseSetupConfiguration() {
		super();
		targetBrowser = "chrome";
		targetEnv = "test";
		targetTags = "";
		pathChrome = null;
		pathFirefox = null;
		pathIE = null;
		pathEdge = null;
		findElementTimeout = 1.0;
		pageLoadTimeout = 60.0;
		scriptTimeout = 60.0;
		windowPos = "maximize";
		closeBrowser = true;
		yamlClasspathDir = "cucumber/miriade/yaml/";
		screenshotDir = "screenshots/";
		onFail = "do-nothing";
		useProperties = "";
		// Cucumber options - - - - - - - - - - - - - - - - - - - -
		cucumberGlueClasspth = CucumberSpec.GLUE;
		cucumberFeaturesPath = CucumberSpec.FEATURES;
	}

	public CuseSetupConfiguration(String targetBrowser, String targetEnv, String targetTags, String pathChrome, String pathFirefox, String pathIE, String pathEdge, Double findElementTimeout, Double pageLoadTimeout, Double scriptTimeout,
		String windowPos, Boolean closeBrowser, String yamlClasspathDir, String screenshotDir, String onFail, String useProperties, String cucumberGlueClasspth, String cucumberFeaturesPath) {
		this();
		if (targetBrowser != null)
			this.targetBrowser = targetBrowser;
		if (targetEnv != null)
			this.targetEnv = targetEnv;
		if (targetTags != null)
			this.targetTags = targetTags;
		if (pathChrome != null)
			this.pathChrome = pathChrome;
		if (pathFirefox != null)
			this.pathFirefox = pathFirefox;
		if (pathIE != null)
			this.pathIE = pathIE;
		if (pathEdge != null)
			this.pathEdge = pathEdge;
		if (findElementTimeout != null)
			this.findElementTimeout = findElementTimeout;
		if (pageLoadTimeout != null)
			this.pageLoadTimeout = pageLoadTimeout;
		if (scriptTimeout != null)
			this.scriptTimeout = scriptTimeout;
		if (windowPos != null)
			this.windowPos = windowPos;
		if (closeBrowser != null)
			this.closeBrowser = closeBrowser;
		if (yamlClasspathDir != null)
			this.yamlClasspathDir = yamlClasspathDir.endsWith("/") ? yamlClasspathDir : yamlClasspathDir + "/";
		if (screenshotDir != null)
			this.screenshotDir = screenshotDir;
		if (onFail != null)
			this.onFail = onFail;
		if (useProperties != null)
			this.useProperties = useProperties;
		// Cucumber options - - - - - - - - - - - - - - - - - - - -
		if (cucumberGlueClasspth != null)
			this.cucumberGlueClasspth = cucumberGlueClasspth;
		if (cucumberFeaturesPath != null)
			this.cucumberFeaturesPath = cucumberFeaturesPath;
	}

	/**
	 * Metodo invocato all'inizializzazione del bean
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		log.debug(this.toString());
	}

	@Override
	protected CuseSetupConfiguration clone() throws CloneNotSupportedException {
		CuseSetupConfiguration conf = new CuseSetupConfiguration(targetBrowser, targetEnv, targetTags, pathChrome, pathFirefox, pathIE, pathEdge, findElementTimeout, pageLoadTimeout, scriptTimeout, windowPos, closeBrowser, yamlClasspathDir,
			screenshotDir, onFail, useProperties, cucumberGlueClasspth, cucumberFeaturesPath);
		return conf;
	}

	/**
	 * JSON-like syntax
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": {\n\t targetBrowser: \"" + targetBrowser + "\",\n\t targetEnv: \"" + targetEnv + "\",\n\t targetTags: \"" + targetTags + "\",\n\t pathChrome: \"" + pathChrome + "\",\n\t pathFirefox: \"" + pathFirefox
			+ "\",\n\t pathIE: \"" + pathIE + "\",\n\t pathEdge: \"" + pathEdge + "\",\n\t findElementTimeout: " + findElementTimeout + ",\n\t pageLoadTimeout: " + pageLoadTimeout + ",\n\t scriptTimeout: " + scriptTimeout + ",\n\t windowPos: \""
			+ windowPos + "\",\n\t closeBrowser: " + closeBrowser + ",\n\t yamlClasspathDir: \"" + yamlClasspathDir + "\",\n\t screenshotDir: \"" + screenshotDir + "\",\n\t onFail: \"" + onFail + "\",\n\t useProperties: \"" + useProperties
			+ "\",\n\t cucumberGlueClasspth: \"" + cucumberGlueClasspth + "\",\n\t cucumberFeaturesPath: \"" + cucumberFeaturesPath + "\" \n}";
	}

	public String targetBrowser() {
		return targetBrowser;
	}

	public void targetBrowser(String targetBrowser) {
		this.targetBrowser = targetBrowser;
	}

	public void targetBrowser(Browser brw) {
		this.targetBrowser = brw.name();
	}

	public String targetEnv() {
		return targetEnv;
	}

	public void targetEnv(String targetEnv) {
		this.targetEnv = targetEnv;
	}

	public String targetTags() {
		return targetTags;
	}

	public void targetTags(String targetTags) {
		this.targetTags = targetTags;
	}

	/**
	 * In caso il path sia nullo, e' possibile delegare la ricerca del path locale del webdriver al sistema operativo.
	 * 
	 * @see CuseDefaultSpec#INSPECT_OS
	 * @see CuseDefaultSpec#INSPECT_OS_ENABLED
	 * @return
	 */
	public String pathChrome() {
		return StringUtils.hasText(pathChrome) || System.getProperty(INSPECT_OS) == null ? pathChrome : SetupUtil.inspectOsForDriverPath(Browser.CHROME);
	}

	public void pathChrome(String pathChrome) {
		this.pathChrome = pathChrome;
	}

	/**
	 * In caso il path sia nullo, e' possibile delegare la ricerca del path locale del webdriver al sistema operativo.
	 * 
	 * @see CuseDefaultSpec#INSPECT_OS
	 * @return
	 */
	public String pathFirefox() {
		return StringUtils.hasText(pathFirefox) || System.getProperty(INSPECT_OS) == null ? pathFirefox : SetupUtil.inspectOsForDriverPath(Browser.FIREFOX);
	}

	public void pathFirefox(String pathFirefox) {
		this.pathFirefox = pathFirefox;
	}

	/**
	 * In caso il path sia nullo, e' possibile delegare la ricerca del path locale del webdriver al sistema operativo.
	 * 
	 * @see CuseDefaultSpec#INSPECT_OS
	 * @return
	 */
	public String pathIE() {
		return StringUtils.hasText(pathIE) || System.getProperty(INSPECT_OS) == null ? pathIE : SetupUtil.inspectOsForDriverPath(Browser.IE);
	}

	public void pathIE(String pathIE) {
		this.pathIE = pathIE;
	}

	/**
	 * In caso il path sia nullo, e' possibile delegare la ricerca del path locale del webdriver al sistema operativo.
	 * 
	 * @see CuseDefaultSpec#INSPECT_OS
	 * @return
	 */
	public String pathEdge() {
		return StringUtils.hasText(pathEdge) || System.getProperty(INSPECT_OS) == null ? pathEdge : SetupUtil.inspectOsForDriverPath(Browser.EDGE);
	}

	public void pathEdge(String pathEdge) {
		this.pathEdge = pathEdge;
	}

	public double timeoutFindElement() {
		return findElementTimeout;
	}

	public void timeoutFindElement(double findElementTimeout) {
		this.findElementTimeout = findElementTimeout;
	}

	public double timeoutPageLoad() {
		return pageLoadTimeout;
	}

	public void timeoutPageLoad(double pageLoadTimeout) {
		this.pageLoadTimeout = pageLoadTimeout;
	}

	public double timeoutScript() {
		return scriptTimeout;
	}

	public void timeoutScript(double scriptTimeout) {
		this.scriptTimeout = scriptTimeout;
	}

	public String windowPos() {
		return windowPos;
	}

	public void windowPos(String windowPos) {
		this.windowPos = windowPos;
	}

	public boolean closeBrowser() {
		return closeBrowser;
	}

	public void closeBrowser(boolean closeBrowser) {
		this.closeBrowser = closeBrowser;
	}

	public String onFail() {
		return StringUtils.hasText(onFail) ? onFail : "do-nothing";
	}

	public void yamlClasspathDir(String yamlClasspath) {
		this.yamlClasspathDir = yamlClasspath;
	}

	public String screenshotDir() {
		return screenshotDir;
	}

	public void screenshotDir(String screenshotDir) {
		this.screenshotDir = screenshotDir;
	}

	public void onFail(String onFail) {
		this.onFail = onFail;
	}

	public String yamlClasspathDir() {
		return yamlClasspathDir;
	}

	public String useProperties() {
		return useProperties;
	}

	/**
	 * <strong>ATTENZIONE</strong>: impostare un property file a runtime non ha nessun effetto! Viene letto solo durante
	 * l'inizializzazione del contesto di Spring
	 * 
	 * @param useProperties
	 */
	@Deprecated
	public void useProperties(String useProperties) {
		this.useProperties = useProperties;
	}

	/**
	 * @return Classpath dove cercare <i>step definitions, hooks and plugins</i> (definito <i>glue code</i>). Cerca
	 *         anche nelle sub directories.
	 */
	public String cucumberGlueClasspth() {
		return cucumberGlueClasspth;
	}

	public void cucumberGlueClasspth(String cucumberGlueClasspth) {
		this.cucumberGlueClasspth = cucumberGlueClasspth;
	}

	/**
	 * @return
	 * 		Path da dove caricare i file con estensione "<i>.feature</i>". Cerca anche nelle sub directories.
	 */
	public String cucumberFeaturesPath() {
		return cucumberFeaturesPath;
	}

	public void cucumberFeaturesPath(String cucumberFeaturesPath) {
		this.cucumberFeaturesPath = cucumberFeaturesPath;
	}

	public void cucumberFeaturesClasspath(String cucumberFeaturesClasspath) {
		this.cucumberFeaturesPath = "classpath:" + cucumberFeaturesClasspath;
	}

}
