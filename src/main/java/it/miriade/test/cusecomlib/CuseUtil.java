package it.miriade.test.cusecomlib;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import it.miriade.test.cusecomlib.cucumber.stepdefs.Stepdef;
import it.miriade.test.cusecomlib.enums.BySelector;
import it.miriade.test.cusecomlib.excep.PollingTimeoutException;
import it.miriade.test.cusecomlib.excep.SeleniumElementNotFoundException;
import it.miriade.test.cusecomlib.excep.YamlInvalidValueException;
import it.miriade.test.cusecomlib.hooks.HtmlHook;
import it.miriade.test.cusecomlib.hooks.HtmlHookFactory;
import it.miriade.test.cusecomlib.selenium.SeleniumDebugUtil;
import it.miriade.test.cusecomlib.selenium.SeleniumWebDriverWrapper;
import it.miriade.test.cusecomlib.times.Times;
import it.miriade.test.cusecomlib.times.TimesFactory;
import it.miriade.test.cusecomlib.times.WhileTrueAction;
import it.miriade.test.cusecomlib.utils.ReflectionUtil;
import it.miriade.test.cusecomlib.yaml.YamlSupport;
import it.miriade.test.cusecomlib.yaml.YamlSupportFactory;

/**
 * Utility per l'utilizzo del {@link WebDriver} di Selenium.<br/>
 * Oggetto che espone metodi per navigare e maneggiare il DOM della pagina web, ovvero un refactoring Java della
 * libreria utilizzata in Ruby <a href="https://github.com/sbos61/Xover">Xover</a>. Internamente si appoggia al
 * {@link SeleniumWebDriverWrapper}.
 * 
 * @see SeleniumWebDriverWrapper
 * @author svaponi
 */
public class CuseUtil implements CuseDefaultSpec, Closeable {

	/**
	 * Se TRUE, viene lanciata una eccezione {@link SeleniumElementNotFoundException} in caso di nessun elemento trovato
	 * in
	 * seguito ad una invocazione di {@link #findBy(HtmlHook)}, {@link #clickBy(HtmlHook)} e
	 * {@link #setTextBy(HtmlHook, String)}
	 * (ovvero i metodi che usano findBy internamente).
	 */
	public static final boolean throw_ex_if_not_found = true;

	/**
	 * Se TRUE, in seguito ad una invocazione di findBy, clickBy e setTextBy, vengono tornati solo gli elementi che
	 * superno l'invocazione di {@link #isGoodElement(WebElement)}.
	 */
	public static final boolean return_only_good_elems = true;

	private static final Pattern queryStringParserRegex = Pattern.compile("[\\?&]([^&=]+)=([^&=]+)");
	private static final Pattern queryStringRegex = Pattern.compile("^([^?]*)?(.*)$");
	private static final NumberFormat decimal = new DecimalFormat("#0.00");
	private final Logger log = LoggerFactory.getLogger(getClass());

	// oggetti sicuramente not null
	private final SeleniumWebDriverWrapper wrapper;
	private final CuseSetupConfiguration config;

	// oggetti che possono essere null (se non uso YAML)
	private YamlSupportFactory yamlSupportFactory;
	private YamlSupport commonYaml;
	private YamlSupport targetEnvYaml;

	/**
	 * @param config
	 *            {@link CuseSetupConfiguration}
	 */
	public CuseUtil(CuseSetupConfiguration config) {
		super();
		this.config = config;
		this.wrapper = new SeleniumWebDriverWrapper(config);
	}

	/**
	 * @param config
	 *            {@link CuseSetupConfiguration}
	 * @param yamlSupportFactory
	 *            {@link YamlSupportFactory} per inizializzare gli {@link YamlSupport}
	 */
	public CuseUtil(CuseSetupConfiguration config, YamlSupportFactory yamlSupportFactory) {
		super();
		this.config = config;
		this.wrapper = new SeleniumWebDriverWrapper(config);
		// se uso YamlSupportFactory deve essere not null! Altrimenti uso altro costruttore
		Assert.notNull(yamlSupportFactory, "yamlSupportFactory is null");
		this.yamlSupportFactory = yamlSupportFactory;
	}

	/**
	 * Metodo che implementa l'interfaccia {@link Closeable}. Questo metodo viene ricunosciuto da Spring che lo usare
	 * per distruggere l'oggeto alla fine del lifecycle. Nel nostro caso chiuderà il browser.
	 * 
	 * @see SeleniumWebDriverWrapper#close()
	 */
	@Override
	public void close() {
		wrapper.close();
	}

	/**
	 * Close the current window, quitting the browser if it's the last window
	 * currently open.
	 */
	public void closeWindow() {
		wrapper.closeWindow();
	}

	/**
	 * Quits this driver, closing every associated window.
	 */
	public void quitBrowser() {
		wrapper.quit();
	}

	/**
	 * Ritorna l'istanza sottostante del {@link WebDriver}. Se non c'è nessun driver attivo lo inizializza.
	 */
	public WebDriver driver() {
		if (!wrapper.isActive())
			wrapper.setup();
		return wrapper.get();
	}

	/**
	 * @return Ritorna il {@link SeleniumWebDriverWrapper} che contiene le informazioni con cui è stato inizializzato il
	 *         il {@link WebDriver} (browser, modalità remote, ecc..).
	 */
	public SeleniumWebDriverWrapper driverWrapper() {
		return wrapper;
	}

	// CuseSetupConfiguration
	// =================================================================================

	/**
	 * {@link CuseSetupConfiguration} con tutti i dati della corrente esecuzione.
	 * 
	 * @return
	 */
	public CuseSetupConfiguration config() {
		return config;
	}

	// YamlSupport
	// ========================================================================

	/**
	 * @return
	 * 		{@link YamlSupport} comune per i dati trasversali agli ambienti (es. i gli hook della UI)
	 * @throws UnsupportedOperationException
	 *             se manca lo YAML
	 */
	public YamlSupport commonYaml() throws UnsupportedOperationException {
		if (commonYaml != null)
			return commonYaml;
		else if (yamlSupportFactory != null)
			try {
				commonYaml = yamlSupportFactory.build(CuseDefaultSpec.YAML_SUPPORT_COMMON_FILENAME);
				if (commonYaml != null)
					return commonYaml;
			} catch (Exception e) {
				log.warn(e.getMessage(), e);
			}
		throw new UnsupportedOperationException(CuseDefaultSpec.YAML_UNSUPPORTED_MESSAGE);
	}

	/**
	 * @return
	 * 		{@link YamlSupport} per i dati dell'ambiente: test, collaudo, ecc..
	 * @throws UnsupportedOperationException
	 *             se manca lo YAML
	 */
	public YamlSupport targetEnvYaml() throws UnsupportedOperationException {
		if (targetEnvYaml != null)
			return targetEnvYaml;
		else if (yamlSupportFactory != null)
			try {
				targetEnvYaml = yamlSupportFactory.build(config.targetEnv());
				if (targetEnvYaml != null)
					return targetEnvYaml;
			} catch (Exception e) {
				log.warn(e.getMessage());
			}
		throw new UnsupportedOperationException(CuseDefaultSpec.YAML_UNSUPPORTED_MESSAGE);
	}

	// Cookies
	// ========================================================================

	/**
	 * @param cookieName
	 * @return
	 * 		TRUE se il cookie esiste
	 */
	public boolean hasCookie(String cookieName) {
		return driver().manage().getCookieNamed(cookieName) != null;
	}

	/**
	 * @param cookieName
	 * @return
	 * 		il {@link Cookie} corrispondente al nome in input
	 */
	public Cookie getCookie(String cookieName) {
		return driver().manage().getCookieNamed(cookieName);
	}

	// Exists
	// ========================================================================

	/**
	 * Ritorna true se esiste almeno un elemento identificato dal input.
	 * 
	 * @param hook
	 */
	public boolean existsBy(HtmlHook hook) {
		return !findBy(hook).isEmpty();
	}

	/**
	 * Ritorna true se esiste almeno un elemento identificato dal input.
	 * 
	 * @param map
	 *            mappa con i valori per costruire un {@link HtmlHook}, vedi {@link HtmlHook#HtmlHook(Map)}
	 */
	public boolean existsBy(Map<String, ?> map) {
		return !findBy(new HtmlHook(map)).isEmpty();
	}

	/**
	 * Ritorna true se esiste almeno un elemento identificato dal input.
	 * 
	 * @param by
	 * @param expr
	 */
	public boolean existsBy(BySelector by, String expr) {
		return existsBy(new HtmlHook(by, expr));
	}

	/**
	 * Ritorna true se esiste almeno un elemento identificato dal input.
	 * 
	 * @param by
	 *            stringa che identifica una valore di {@link BySelector}
	 * @param expr
	 */
	public boolean existsBy(String by, String expr) {
		return existsBy(BySelector.get(by), expr);
	}

	/**
	 * Come {@link CuseUtil#existsBy(HtmlHook)} però contiene un riferimento al common.yml dal quale estrarre i
	 * {@link HtmlHook#BY} e {@link HtmlHook#EXPR} per custruirsi l'hook. <br/>
	 * <strong>ATTENZIONE</strong>: lo YAML deve avere la struttura standard (vedi documentazione)
	 * 
	 * @param hookName
	 * @return
	 * @throws Exception
	 */
	public boolean existsBy(String hookName) {
		return existsBy(HtmlHookFactory.build(commonYaml(), hookName));
	}

	/**
	 * Come {@link CuseUtil#existsBy(HtmlHook)} però utilizza lo {@link YamlSupport} in input per estrarre i
	 * {@link HtmlHook#BY} e {@link HtmlHook#EXPR} per custruirsi l'hook. <br/>
	 * <strong>ATTENZIONE</strong>: lo YAML deve avere la struttura standard (vedi documentazione)
	 * 
	 * @param yaml
	 * @param hookName
	 * @return
	 */
	public boolean existsBy(YamlSupport yaml, String hookName) {
		return existsBy(HtmlHookFactory.build(yaml, hookName));
	}

	// Find
	// ========================================================================

	/**
	 * Ritorna un array degli elementi identificati dal hook.
	 * 
	 * @param hook
	 * @return
	 */
	public List<WebElement> findBy(HtmlHook hook) {
		List<WebElement> el;
		switch (hook.by) {
		/*
		 * driver().findElements(By....)
		 * This method is affected by the 'implicit wait' times in force at the time of execution. When implicitly
		 * waiting, this method will return as soon as there are more than 0 items in the found collection, or will
		 * return an empty list if the timeout is reached.
		 */
		case CSS:
			el = driver().findElements(By.cssSelector(hook.expr));
			break;
		case ID:
			el = driver().findElements(By.id(hook.expr));
			break;
		case CLASS:
			el = driver().findElements(By.className(hook.expr));
			break;
		case NAME:
			el = driver().findElements(By.name(hook.expr));
			break;
		case TAG_NAME:
			el = driver().findElements(By.tagName(hook.expr));
			break;
		case TEXT:
			el = driver().findElements(By.linkText(hook.expr));
			break;
		case PARTIAL_TEXT:
			el = driver().findElements(By.partialLinkText(hook.expr));
			break;
		case XPATH:
			el = driver().findElements(By.xpath(hook.expr));
			break;
		default:
			throw new RuntimeException("Missing element " + hook);
		}
		if (return_only_good_elems)
			return el.stream().filter(e -> isGoodElement(e)) // filters only good elements
				.collect(Collectors.toList()); // transforms stream to collection
		else
			return el;
	}

	/**
	 * Ritorna un array degli elementi identificati dal input.
	 * 
	 * @param map
	 *            mappa con i valori per costruire un {@link HtmlHook}, vedi {@link HtmlHook#HtmlHook(Map)}
	 * @return
	 */
	public List<WebElement> findBy(Map<String, ?> map) {
		return findBy(new HtmlHook(map));
	}

	/**
	 * Ritorna un array degli elementi identificati dal input.
	 * 
	 * @param by
	 * @param expr
	 * @return
	 */
	public List<WebElement> findBy(BySelector by, String expr) {
		return findBy(new HtmlHook(by, expr));
	}

	/**
	 * Ritorna un array degli elementi identificati dal input.
	 * 
	 * @param by
	 *            stringa che identifica una valore di {@link BySelector}
	 * @param expr
	 * @return
	 */
	public List<WebElement> findBy(String by, String expr) {
		return findBy(BySelector.get(by), expr);
	}

	/**
	 * Come {@link CuseUtil#findBy(HtmlHook)} però contiene un riferimento al common.yml dal quale estrarre i
	 * {@link HtmlHook#BY} e {@link HtmlHook#EXPR} per custruirsi l'hook. <br/>
	 * <strong>ATTENZIONE</strong>: lo YAML deve avere la struttura standard (vedi documentazione)
	 * 
	 * @param hookName
	 * @return
	 * @throws Exception
	 */
	public List<WebElement> findBy(String hookName) {
		return findBy(HtmlHookFactory.build(commonYaml(), hookName));
	}

	/**
	 * Come {@link CuseUtil#existsBy(HtmlHook)} però utilizza lo {@link YamlSupport} in input per estrarre i
	 * {@link HtmlHook#BY} e {@link HtmlHook#EXPR} per custruirsi l'hook. <br/>
	 * <strong>ATTENZIONE</strong>: lo YAML deve avere la struttura standard (vedi documentazione)
	 * 
	 * @param yaml
	 * @param hookName
	 * @return
	 */
	public List<WebElement> findBy(YamlSupport yaml, String hookName) {
		return findBy(HtmlHookFactory.build(yaml, hookName));
	}

	// Click
	// ========================================================================

	/**
	 * Invia un click sull'elemento identificato dal hook.
	 * 
	 * @param hook
	 */
	public void clickBy(HtmlHook hook) {
		boolean found = false;
		for (WebElement el : findBy(hook.by, hook.expr))
			if (isGoodElement(el))
				try {
					el.click();
					found = true;
				} catch (Exception e) {
					log.error("Exception catched while clicking on element " + hook + ": {}", e.getMessage());
				}

		if (!found && throw_ex_if_not_found) {
			throw new RuntimeException("Element " + hook + " not found");
		}
	}

	/**
	 * Invia un click sull'elemento identificato dal input.
	 * 
	 * @param map
	 *            mappa con i valori per costruire un {@link HtmlHook}, vedi {@link HtmlHook#HtmlHook(Map)}
	 */
	public void clickBy(Map<String, ?> map) {
		clickBy(new HtmlHook(map));
	}

	/**
	 * Invia un click sull'elemento identificato dal input.
	 * 
	 * @param by
	 * @param expr
	 */
	public void clickBy(BySelector by, String expr) {
		clickBy(new HtmlHook(by, expr));
	}

	/**
	 * Invia un click sull'elemento identificato dal input.
	 * 
	 * @param by
	 *            stringa che identifica una valore di {@link BySelector}
	 * @param expr
	 */
	public void clickBy(String by, String expr) {
		clickBy(BySelector.get(by), expr);
	}

	/**
	 * Come {@link CuseUtil#existsBy(HtmlHook)} però contiene un riferimento al common.yml dal quale estrarre i
	 * {@link HtmlHook#BY} e {@link HtmlHook#EXPR} per custruirsi l'hook. <br/>
	 * <strong>ATTENZIONE</strong>: lo YAML deve avere la struttura standard (vedi documentazione)
	 * 
	 * @param hookName
	 * @throws Exception
	 */
	public void clickBy(String hookName) {
		clickBy(HtmlHookFactory.build(commonYaml(), hookName));
	}

	/**
	 * Come {@link CuseUtil#existsBy(HtmlHook)} però utilizza lo {@link YamlSupport} in input per estrarre i
	 * {@link HtmlHook#BY} e {@link HtmlHook#EXPR} per custruirsi l'hook. <br/>
	 * <strong>ATTENZIONE</strong>: lo YAML deve avere la struttura standard (vedi documentazione)
	 * 
	 * @param yaml
	 * @param hookName
	 */
	public void clickBy(YamlSupport yaml, String hookName) {
		clickBy(HtmlHookFactory.build(yaml, hookName));
	}

	// Set text
	// ========================================================================

	/**
	 * Imposta il value sull'elemento identificato dal hook.
	 * 
	 * @param hook
	 * @param value
	 */
	public void setTextBy(HtmlHook hook, String value) {
		textBy(hook, value, false);
	}

	/**
	 * Imposta il value sull'elemento identificato dal input.
	 * 
	 * @param map
	 *            mappa con i valori per costruire un {@link HtmlHook}, vedi {@link HtmlHook#HtmlHook(Map)}
	 * @param value
	 */
	public void setTextBy(Map<String, ?> map, String value) {
		setTextBy(new HtmlHook(map), value);
	}

	/**
	 * Imposta il value sull'elemento identificato dal input.
	 * 
	 * @param by
	 * @param expr
	 * @param value
	 */
	public void setTextBy(BySelector by, String expr, String value) {
		setTextBy(new HtmlHook(by, expr), value);
	}

	/**
	 * Imposta il value sull'elemento identificato dal input.
	 * 
	 * @param by
	 *            stringa che identifica una valore di {@link BySelector}
	 * @param expr
	 * @param value
	 */
	public void setTextBy(String by, String expr, String value) {
		setTextBy(BySelector.get(by), expr, value);
	}

	/**
	 * Come {@link CuseUtil#setTextBy(HtmlHook, String)} però contiene un riferimento al common.yml dal quale
	 * estrarre i
	 * {@link HtmlHook#BY} e {@link HtmlHook#EXPR} per custruirsi l'hook. <br/>
	 * <strong>ATTENZIONE</strong>: lo YAML deve avere la struttura standard (vedi documentazione)
	 * 
	 * @param hookName
	 * @param value
	 */
	public void setTextBy(String hookName, String value) {
		setTextBy(HtmlHookFactory.build(commonYaml(), hookName), value);
	}

	/**
	 * Come {@link CuseUtil#setTextBy(HtmlHook, String)} però utilizza lo {@link YamlSupport} in input per
	 * estrarre i
	 * {@link HtmlHook#BY} e {@link HtmlHook#EXPR} per custruirsi l'hook. <br/>
	 * <strong>ATTENZIONE</strong>: lo YAML deve avere la struttura standard (vedi documentazione)
	 * 
	 * @param yaml
	 * @param hookName
	 * @param value
	 */
	public void setTextBy(YamlSupport yaml, String hookName, String value) {
		setTextBy(HtmlHookFactory.build(yaml, hookName), value);
	}

	// Append text
	// =================================================================================

	/**
	 * Imposta il value sull'elemento identificato dal hook.
	 * 
	 * @param hook
	 * @param value
	 */
	public void appendTextBy(HtmlHook hook, String value) {
		textBy(hook, value, true);
	}

	/**
	 * Imposta il value sull'elemento identificato dal input.
	 * 
	 * @param map
	 *            mappa con i valori per costruire un {@link HtmlHook}, vedi {@link HtmlHook#HtmlHook(Map)}
	 * @param value
	 */
	public void appendTextBy(Map<String, ?> map, String value) {
		appendTextBy(new HtmlHook(map), value);
	}

	/**
	 * Imposta il value sull'elemento identificato dal input.
	 * 
	 * @param by
	 * @param expr
	 * @param value
	 */
	public void appendTextBy(BySelector by, String expr, String value) {
		appendTextBy(new HtmlHook(by, expr), value);
	}

	/**
	 * Imposta il value sull'elemento identificato dal input.
	 * 
	 * @param by
	 *            stringa che identifica una valore di {@link BySelector}
	 * @param expr
	 * @param value
	 */
	public void appendTextBy(String by, String expr, String value) {
		appendTextBy(BySelector.get(by), expr, value);
	}

	/**
	 * Come {@link CuseUtil#appendTextBy(HtmlHook, String)} però contiene un riferimento al common.yml dal quale
	 * estrarre i
	 * {@link HtmlHook#BY} e {@link HtmlHook#EXPR} per custruirsi l'hook. <br/>
	 * <strong>ATTENZIONE</strong>: lo YAML deve avere la struttura standard (vedi documentazione)
	 * 
	 * @param hookName
	 * @param value
	 */
	public void appendTextBy(String hookName, String value) {
		appendTextBy(HtmlHookFactory.build(commonYaml(), hookName), value);
	}

	/**
	 * Come {@link CuseUtil#appendTextBy(HtmlHook, String)} però utilizza lo {@link YamlSupport} in input per
	 * estrarre i
	 * {@link HtmlHook#BY} e {@link HtmlHook#EXPR} per custruirsi l'hook. <br/>
	 * <strong>ATTENZIONE</strong>: lo YAML deve avere la struttura standard (vedi documentazione)
	 * 
	 * @param yaml
	 * @param hookName
	 * @param value
	 */
	public void appendTextBy(YamlSupport yaml, String hookName, String value) {
		appendTextBy(HtmlHookFactory.build(yaml, hookName), value);
	}

	// set/append text private methods

	/**
	 * Metodo sottostante usato sia per il {@link #setTextBy(HtmlHook, String)} che per
	 * {@link #appendTextBy(HtmlHook, String)}
	 * 
	 * @param hook
	 * @param value
	 */
	private void textBy(HtmlHook hook, String value, boolean append) {
		boolean found = false;
		for (WebElement el : findBy(hook.by, hook.expr))
			// filtro solo gli elementi "buoni" (necessario per Firefox)
			if (isGoodElement(el)) {
				// se non appendo allora pulisco l'input prima di scrivere
				if (!append)
					try {
						el.clear();
					} catch (Exception e) {
						log.error("Exception catched while clearing text on element " + hook + ": {}", e.getMessage());
					}
				try {
					el.sendKeys(value);
					found = true;
				} catch (Exception e) {
					log.error("Exception catched while setting text to element " + hook + ": {}", e.getMessage());
				}
			}

		if (!found && throw_ex_if_not_found) {
			throw new SeleniumElementNotFoundException("Element " + hook + " not found");
		}
	}

	/**
	 * Indica gli elementi buoni, e quelli da saltare. In sostanza sono gli elementi visualizzati e abilitati, anche se
	 * dipende molto dall'implemetazione del Driver.
	 * 
	 * @param el
	 * @return
	 */
	private boolean isGoodElement(WebElement el) {
		boolean isDisplayed = false, isEnabled = false;
		try {
			isDisplayed = el.isDisplayed();
		} catch (JavascriptException je) {
			log.trace("isDisplayed JavascriptException: {}", je.getMessage());
			isDisplayed = true;
		} catch (Exception e) {
			log.trace("isDisplayed: {}", e.getMessage());
			log.trace(e.getMessage());
		}
		try {
			isEnabled = el.isEnabled();
		} catch (JavascriptException je) {
			log.trace("isEnabled JavascriptException: {}", je.getMessage());
			isEnabled = true;
		} catch (Exception e) {
			log.trace("isEnabled: {}", e.getMessage());
			log.trace(e.getMessage());
		}
		return isDisplayed && isEnabled;
	}

	// Metodi per ispezionare la pagina corrente
	// =================================================================================

	/**
	 * Carica la pagina in input
	 * 
	 * @param url
	 *            indirizzo da caricare
	 */
	public void loadPage(String url) {
		log.info("Loading page {}", url);
		driver().get(url);
	}

	/**
	 * @return il titolo corrente della pagina web
	 */
	public String currentTitle() {
		return driver().getTitle();
	}

	/**
	 * @return path corrente dell'applicazione
	 */
	public String currentUrl() {
		return driver().getCurrentUrl();
	}

	/**
	 * @return la query string dell'url corrente
	 */
	public String currentQueryString() {
		String url = currentUrl();
		Matcher match = queryStringRegex.matcher(url);
		if (match.matches()) {
			String queryString = match.group(2);
			log.trace("Query-string: {}", queryString);
			return queryString;
		}
		return null;
	}

	/**
	 * @return la query string dell'url corrente, parsata e storata in una {@link Map}
	 */
	public Map<String, String> currentQueryStringWithinAMap() {
		String queryString = currentQueryString();
		if (queryString != null) {
			Map<String, String> map = new HashMap<>();
			Matcher m = queryStringParserRegex.matcher(queryString);
			while (m.find()) {
				map.put(m.group(1), m.group(2));
				System.out.printf("%d - %d \n", m.start(), m.end());
				for (int i = 0; i <= m.groupCount(); i++)
					System.out.printf("\t%d) %s \n", i, m.group(i));
			}
			return map;
		}
		return Collections.emptyMap();
	}

	/**
	 * @return path angular corrente (dopo il #)
	 */
	public String currentAngularPath() {
		String url = currentUrl();
		Pattern regexp = Pattern.compile("^([^#]*)#(.*)$");
		Matcher match = regexp.matcher(url);
		if (match.matches()) {
			String path = match.group(2);
			log.trace("Angular path: {}", path);
			return path;
		}
		return null;
	}

	// Screenshots
	// =================================================================================

	static final SimpleDateFormat screenshotDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
	static final String screenshotBasepath = "screenshots/";

	public String timestamp() {
		return screenshotDateFormat.format(new Date());
	}

	/**
	 * Salva uno screenshot in formato png
	 * 
	 * @return torna il {@link File} dell'immagine
	 */
	public File takeScreenshot() {
		try {
			File tmpFile = ((TakesScreenshot) driver()).getScreenshotAs(OutputType.FILE);
			log.debug("Temp screenshot: {}", tmpFile.getAbsolutePath());
			String stepdefName = getStepdefName();
			File screenshot = new File(screenshotBasepath + stepdefName + "." + timestamp() + ".png");
			FileUtils.forceMkdirParent(screenshot);
			FileUtils.copyFile(tmpFile, screenshot);
			log.info("New screenshot: {}", screenshot.getName());
			return screenshot;
		} catch (IOException e) {
			e.printStackTrace();
			log.error("Screenshot error: {}", e.getMessage());
		}
		return null;
	}

	// Metodi Wait
	// =================================================================================

	/**
	 * Mette in attesa l'esecuzione
	 * 
	 * @param millis
	 *            durata dell'interruzione in millisecondi
	 */
	public void waitMillis(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * Mette in attesa l'esecuzione per l'intervallo {@link Times#DELAY} dell'input
	 * 
	 * @param time
	 *            contiene il tempo di attesa
	 */
	public void wait(Times time) {
		waitMillis(time.delay);
	}

	/**
	 * Come {@link #wait(Times)} con un moltiplicatore per aumentre proporzionalmente l'intervallo
	 * 
	 * @see #wait(Times)
	 * @param time
	 *            contiene il tempo di attesa
	 * @param n
	 *            moltiplicatore
	 */
	public void wait(Times time, int n) {
		waitMillis(time.delay * n);
	}

	/**
	 * Mette in attesa l'esecuzione per l'intervallo associato as {@link Times#DELAY} preso dallo YAML
	 * 
	 * @param key
	 */
	public void wait(String key) {
		wait(TimesFactory.build(commonYaml(), key));
	}

	/**
	 * Come {@link #wait(String)} con lo {@link YamlSupport} privato in input.
	 * 
	 * @param yaml
	 *            {@link YamlSupport} da usare al posto del common.yml per prelevare le informazioni.
	 * @param key
	 */
	public void wait(YamlSupport yaml, String key) {
		wait(TimesFactory.build(yaml, key));
	}

	/**
	 * Come {@link #wait(String)} con la possibilià di moltiplicare il valore dell'intervallo. E' utile perchè definiamo
	 * un solo intervallo che sarà la nostra base(x 1), poi andiamo a moltiplicarlo per n a seconda di dove lo usiamo,
	 * in questo modo manteniamo facilmente una
	 * proporzionalità negli intervalli.
	 * 
	 * @param key
	 * @param n
	 *            moltiplicatore
	 */
	public void wait(String key, int n) {
		wait(TimesFactory.build(commonYaml(), key), n);
	}

	/**
	 * Come {@link #wait(String, int)} con lo {@link YamlSupport} privato in input.
	 * 
	 * @param yaml
	 *            {@link YamlSupport} da usare al posto del common.yml per prelevare le informazioni.
	 * @param key
	 * @param n
	 *            moltiplicatore
	 */
	public void wait(YamlSupport yaml, String key, int n) {
		wait(TimesFactory.build(yaml, key), n);
	}

	// Polling
	// =================================================================================

	/**
	 * Metodo di polling. Continua a svolgere una azione ad intervalli regolari finche non da esito positivo. L'azione è
	 * implementata da un oggetto {@link WhileTrueAction}.
	 * 
	 * @see WhileTrueAction
	 * @param action
	 *            azione da compiere
	 * @param times
	 *            contiene i tempi del polling
	 */
	public void polling(WhileTrueAction action, Times times) {
		pollingWithLog(action, "?", times);
	}

	/**
	 * Metodo di polling. Continua a svolgere una azione ad intervalli regolari finche non da esito positivo. L'azione è
	 * implementata da un oggetto {@link WhileTrueAction}.
	 * 
	 * @see WhileTrueAction
	 * @param action
	 *            azione da compiere
	 * @param args
	 *            caratteristiche temporali del polling
	 */
	public void polling(WhileTrueAction action, Object... args) {
		pollingWithLog(action, "?", args);
	}

	/**
	 * Invertito l'ordine dei parametri per disambiguare la stringa di log dai parametri del vararg. <br/>
	 * Sostituire con {@link #pollingWithLog(String, WhileTrueAction, Object...)}
	 * 
	 * @see #pollingWithLog(String, WhileTrueAction, Object...)
	 * @param action
	 * @param logMessage
	 * @param args
	 */
	@Deprecated
	public void pollingWithLog(WhileTrueAction action, String logMessage, Object... args) {
		pollingWithLog(logMessage, action, args);
	}

	/**
	 * Invertito l'ordine dei parametri per adeguarsi ad {@link #pollingWithLog(String, WhileTrueAction, Object...)}.
	 * <br/>
	 * Sostituire con {@link #pollingWithLog(String, WhileTrueAction, Times)}
	 * 
	 * @see #pollingWithLog(String, WhileTrueAction, Times)
	 * @param action
	 * @param logMessage
	 * @param times
	 */
	@Deprecated
	public void pollingWithLog(WhileTrueAction action, String logMessage, Times times) {
		pollingWithLog(logMessage, action, times);
	}

	/**
	 * Come {@link #polling(WhileTrueAction, Object...)} con in più il messaggio da stampare nel log ad ogni esecuzione.
	 * 
	 * @see WhileTrueAction
	 * @param logMessage
	 *            messaggio da riportare nel log
	 * @param action
	 *            azione da compiere
	 * @param args
	 *            caratteristiche temporali del polling
	 */
	public void pollingWithLog(String logMessage, WhileTrueAction action, Object... args) {
		pollingWithLog(logMessage, action, TimesFactory.build(commonYaml(), args));
	}

	/**
	 * Come {@link #polling(WhileTrueAction, Object...)} con in più il messaggio da stampare nel log ad ogni esecuzione.
	 * 
	 * @see WhileTrueAction
	 * @param logMessage
	 *            messaggio da riportare nel log
	 * @param action
	 *            azione da compiere
	 * @param times
	 *            contiene i tempi del polling
	 */
	public void pollingWithLog(String logMessage, WhileTrueAction action, Times times) {

		log.debug("Inizio polling \"{}\" - {}", logMessage, times);

		if (times.startAfter == null)
			log.info("{}...", logMessage);
		else
			log.info("{}... (comincio dopo {}s)", logMessage, decimal.format((double) times.startAfter / 1000));

		// times.start_after = secondi dopo di cui cominciare il polling
		if (times.startAfter != null && times.startAfter > 0)
			waitMillis(times.startAfter);

		double waiting_time = 0;
		while (action.isTrue() && waiting_time < times.maxWaitingTime) {
			waitMillis(times.delay);
			waiting_time += times.delay;
			log.info("{}... ({}s)", logMessage, decimal.format((double) waiting_time / 1000));
		}

		if (waiting_time >= times.maxWaitingTime)
			throw new PollingTimeoutException("Ho aspettato troppo! (limite " + decimal.format(times.maxWaitingTime) + "s)");
	}

	/**
	 * Ritorna un {@link Pattern} compilato con la regex presa dallo YAML
	 * 
	 * @param regexkey
	 *            chiave per prendere la regex
	 * @return
	 */
	public Pattern regex(String regexkey) {
		String regex = commonYaml().getString("?.?", YAML_SUPPORT_REGEXP_PREFIX, regexkey);
		if (!StringUtils.hasText(regex))
			throw new YamlInvalidValueException("Manca la regex con chiave \"" + regexkey + "\"");
		return Pattern.compile(regex);
	}

	/**
	 * Fa il merge dell'input con la regex presa dallo YAML. <br>
	 * <h3>Esempio</h3>
	 * Questo è un estratto del contenuto dello YAML:
	 * 
	 * <pre>
	 * regex:
	 * 	tab_css_selector: "tabs ul > li[heading='$1']"
	 * </pre>
	 * 
	 * Con il seguente codice posso selezionare il mio tab preferito.
	 * 
	 * <pre>
	 * String css_selector = util.mergeIntoRegex("tab_css_selector", "my-favorite-tab");
	 * seleniumUtil.clickBy(BySelector.CSS, css_selector);
	 * </pre>
	 * 
	 * Il valore di css_selector è "tabs ul > li[heading='my-favorite-tab']".
	 * 
	 * @param regexkey
	 * @param input
	 * @return
	 */
	public String mergeIntoRegex(String regexkey, String input) {
		String regex = commonYaml().getString("?.?", YAML_SUPPORT_REGEXP_PREFIX, regexkey);
		if (!StringUtils.hasText(regex))
			throw new YamlInvalidValueException("Manca la regex con chiave \"" + regexkey + "\"");

		// sostituisco eventuali '\\1' (usati in Ruby) con '$1' (usati in Java)
		String javaAdaptedRegexp = regex.replaceAll("\\", "$");
		return input.replaceAll("^(.*)$", javaAdaptedRegexp);
	}

	// Utility
	// =================================================================================

	/**
	 * Trova lo stepdef che sta girando nel momento dell'invocazione, e torna il nome in stringa formattato per essere
	 * usato nei log e/o testo delle eccezioni.
	 * 
	 * @return torna il nome della classe e il metodo dello stepdef
	 */
	public static String getStepdefName() {
		try {
			StackTraceElement stepdef = getStepdefStackElement();
			return ReflectionUtil.formatClassAndMethod(stepdef);
		} catch (Exception e) {
			return "UNKNOWN_STEPDEF";
		}
	}

	/**
	 * Cerca nello stacktrace lo stepdef che sta girando nel momento dell'invocazione.
	 * 
	 * @return
	 */
	public static StackTraceElement getStepdefStackElement() {
		return ReflectionUtil.getStackElement(Stepdef.class);
	}

	// Failing
	// =================================================================================

	/**
	 * Forza il fallimento dello step corrente lanciando una eccezione.
	 * <br/>
	 * E' possibile bloccare l'esecuzione dei test prima che venga sollevata l'eccezione, così è resa disponibile allo
	 * sviluppatore l'esatta situazione della pagina che ha generato l'errore. Vedi {@link #toDoOnFail()}.
	 * 
	 * @param reason
	 */
	public void failStep(String reason) {
		StackTraceElement caller = ReflectionUtil.getStackElement(Stepdef.class);
		failStep(ReflectionUtil.formatClassMethodAndLineNumber(caller), reason);
	}

	/**
	 * Forza il fallimento dello step corrente lanciando una eccezione.
	 * <br/>
	 * E' possibile bloccare l'esecuzione dei test prima che venga sollevata l'eccezione, così è resa disponibile allo
	 * sviluppatore l'esatta situazione della pagina che ha generato l'errore. Vedi {@link #toDoOnFail()}.
	 * 
	 * @param stepname
	 * @param reason
	 */
	public void failStep(String stepname, String reason) {
		String text = String.format("Step \"%s\" fallito! %s", stepname, reason);
		throwAndLog(text);
	}

	/**
	 * Logga il messaggio (eventualmente inniettando gli args) e poi solleva una eccezione.
	 * <br/>
	 * E' possibile bloccare l'esecuzione dei test prima che venga sollevata l'eccezione, così è resa disponibile allo
	 * sviluppatore l'esatta situazione della pagina che ha generato l'errore. Vedi {@link #toDoOnFail()}.
	 * 
	 * @param message
	 *            messaggio da loggare
	 * @param args
	 *            argomenti da innettare nel messaggio
	 * @throws RuntimeException
	 */
	public void throwAndLog(String message, Object... args) throws RuntimeException {
		String text = args.length == 0 ? message : String.format(message.replaceAll("\\{\\}", "%s"), args);
		log.info(text);
		/*
		 * toDoOnFail() tolto da qui e messo un un @After hook in cucumber.miriade.stepdefs.ContextConfigStepdef
		 */
		// toDoOnFail();
		throw new RuntimeException(text);
	}

	/**
	 * @see SeleniumDebugUtil
	 */
	public void debug() {
		SeleniumDebugUtil.start(this);
	}

	/**
	 * Comportamento da invocare quando i test falliscono.<br/>
	 * Può bloccare l'esecuzione dei test quando viene sollevata una eccezione mettendo a disposizione l'esatta
	 * situazione della pagina che ha generato l'errore. E' possibile invocare diverse azioni tramite la property
	 * {@link CuseDefaultSpec#WHAT_TO_DO_ON_FAIL}.
	 * <ul>
	 * <li>do-nothing => continua l'esecuzione senza fermarsi</li>
	 * <li>debug => lancia la {@link SeleniumDebugUtil}</li>
	 * <li>freeze => blocca lo schermo finchè non premo invio</li>
	 * <li>exit => termina brutalmente l'esecuzine dei test</li>
	 * <ul>
	 */
	public final void toDoOnFail() {
		switch (config.onFail()) {
		case "debug":
			debug();
			break;

		case "freeze":
			try {
				System.out.println("\nPremi INVIO per proseguire...");
				System.in.read();
			} catch (IOException e) {
			}
			break;

		case "exit":
			System.exit(1);
			break;

		case "???":
			// qui possiamo inserire altre azioni utili da utilire come debug
			break;

		}
	}
}