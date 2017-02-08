package it.miriade.test.cusecomlib.selenium;

import java.io.Closeable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import it.miriade.test.cusecomlib.CuseDefaultSpec;
import it.miriade.test.cusecomlib.CuseSetupConfiguration;
import it.miriade.test.cusecomlib.enums.Browser;
import it.miriade.test.cusecomlib.excep.SeleniumSetupException;

/**
 * Si occupa di inizializzare il {@link WebDriver} di Selenium ed incapsula le sue caratteristiche (browser, modalità
 * remote, ecc..).
 * 
 * @author svaponi
 */
public class SeleniumWebDriverWrapper implements CuseDefaultSpec, Closeable {

	/*
	 * NOTA BENE: se usiamo l'interfaccia Closeable sarà il contesto di Spring alla fine del suo lifecycle a invocare il
	 * metodo close()
	 */

	private final Logger log = LoggerFactory.getLogger(getClass());
	private static final Pattern isRemoteRegex = Pattern.compile("^.*(http[s]?://(.*))$");

	private CuseSetupConfiguration config;
	private WebDriver driver;
	private Browser browser;
	private boolean isRemote;
	private boolean disableCloseBrowser;

	public SeleniumWebDriverWrapper(CuseSetupConfiguration config) {
		super();
		this.config = config;
	}

	/**
	 * JSON-like syntax
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": {\n\t browser: \"" + browser + "\",\n\t isRemote: " + isRemote + " \n}";
	}

	/**
	 * Metodo che implementa l'interfaccia {@link Closeable}. Quando invocato chiude il WebDriver e di conseguenza alche
	 * il browser. <br/>
	 * Può risultare utile al di fuori del contesto Spring, esempio:
	 * 
	 * <pre>
	 * CuseSetupConfiguration config = new CuseSetupConfiguration();
	 * config.targetBrowser(Browser.FIREFOX);
	 * config.pathChrome("path/to/driver");
	 * try (SeleniumWebDriverWrapper wrapper = new SeleniumWebDriverWrapper(config)) {
	 * 	wrapper.get().findElement(By.cssSelector("ul > li.active a")).click();
	 * }
	 * </pre>
	 * 
	 * Nell'esempio andiamo ad inizializzare il wrapper ed utilizzarlo in un blocco try-with-resources. Alla fine del
	 * blocco, poichè è {@link Closeable}, il driver viene chiuso e di conseguenza alche il browser.
	 *
	 * @see #quit()
	 */
	@Override
	public void close() {
		if (disableCloseBrowser) {
			log.warn("BROWSER STAYS OPEN because of \"{}\" = false", CLOSE_BROWSER);
		} else {
			quit();
		}
	}

	/**
	 * Chiude il {@link WebDriver} e di conseguenza anche il browser associato.
	 */
	public void quit() {
		log.info("Quitting {} browser...", browser);
		if (isActive()) {
			driver.quit();
			driver = null;
		} else {
			log.debug("WebDriver already closed");
		}
	}

	/**
	 * Chiude la finestra corrente, se è l'ultima aperta viene chiuso anche il {@link WebDriver}.
	 */
	public void closeWindow() {
		log.info("Closing {} window...", browser);
		if (isActive()) {
			driver.close();
			if (!isActive())
				driver = null;
		} else {
			log.debug("WebDriver already closed");
		}
	}

	/**
	 * Ritorna il browser sul quale girano i test
	 * 
	 * @return
	 */
	public Browser getBrowser() {
		return browser;
	}

	/**
	 * Indica se stiamo usando un {@link RemoteWebDriver}
	 * 
	 * @return
	 */
	public boolean isRemote() {
		return isRemote;
	}

	/**
	 * Ritorna l'istanza singleton del {@link WebDriver}.
	 * 
	 * @return
	 * @throws SeleniumSetupException
	 *             eccezione sollevata quando il driver non è attivo/inizializzato
	 */
	public WebDriver get() throws SeleniumSetupException {
		if (!isActive())
			throw new SeleniumSetupException("Uninitialized WebDriver");
		return driver;
	}

	/**
	 * Mi dice se il wrapper sta tenendo una istanza attiva di {@link WebDriver}
	 * 
	 * @return
	 */
	public boolean isActive() {
		return driver != null && driver instanceof RemoteWebDriver && ((RemoteWebDriver) driver).getSessionId() != null;
	}

	/**
	 * Inizializza il {@link WebDriver}.
	 * 
	 * @throws SeleniumSetupException
	 *             eccezione sollevata se qualcosa va storto durante la inizializzazione del {@link WebDriver}
	 */
	public void setup() throws SeleniumSetupException {

		log.debug("Inizio setup del WebDriver...");

		try {

			Assert.notNull(config, "Missing setup configuration");

			String targetBrowser = config.targetBrowser();
			disableCloseBrowser = !config.closeBrowser();

			/*
			 * Inizializzazione del WebDriver
			 * - - - - - - - - - - - - - - - - - - - -
			 * https://github.com/SeleniumHQ/www.seleniumhq.org
			 * https://raw.githubusercontent.com/wiki/SeleniumHQ/selenium/DesiredCapabilities.md
			 */

			Assert.hasText(targetBrowser, "Missing '" + TARGET_BROWSER + "' property");
			try {
				browser = Browser.valueOf(targetBrowser.toUpperCase());
				log.info("Browser to use: {}", browser.name());
			} catch (Exception e) {
				throw new RuntimeException("Invalid browser: " + targetBrowser);
			}

			Matcher match;
			switch (browser) {
			case CHROME:
				String pathChrome = config.pathChrome();
				validateWebDriverPath(WEBDRIVER_PATH_CHROME, pathChrome);
				match = isRemoteRegex.matcher(pathChrome);
				isRemote = match.matches();
				if (isRemote)
					try {
						URL url = new URL(match.group(1));
						log.info("Using remote driver, URL: {}", url);
						DesiredCapabilities capabilities = DesiredCapabilities.chrome();
						// ADD CAPABILITIES HERE...
						driver = new RemoteWebDriver(url, capabilities);
					} catch (MalformedURLException e) {
						throw new RuntimeException("Impossibile caricare il driver di Chrome: " + e.getMessage());
					}
				else
					driver = new ChromeDriver();

				break;

			case FIREFOX:
				String pathFirefox = config.pathFirefox();
				validateWebDriverPath(WEBDRIVER_PATH_FIREFOX, pathFirefox);
				match = isRemoteRegex.matcher(pathFirefox);
				isRemote = match.matches();
				if (isRemote)
					try {
						URL url = new URL(match.group(1));
						log.info("Using remote driver, URL {}", url);
						DesiredCapabilities capabilities = DesiredCapabilities.firefox();
						// ADD CAPABILITIES HERE...
						driver = new RemoteWebDriver(url, capabilities);
					} catch (MalformedURLException e) {
						throw new RuntimeException("Impossibile caricare il driver di Firefox: " + e.getMessage());
					}
				else
					driver = new FirefoxDriver();
				break;

			case IE:
				String pathIE = config.pathIE();
				validateWebDriverPath(WEBDRIVER_PATH_IE, pathIE);
				match = isRemoteRegex.matcher(pathIE);
				isRemote = match.matches();
				if (isRemote)
					try {
						URL url = new URL(match.group(1));
						log.info("Using remote driver, URL {}", url);
						DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
						// ADD CAPABILITIES HERE...
						driver = new RemoteWebDriver(url, capabilities);
					} catch (MalformedURLException e) {
						throw new RuntimeException("Impossibile caricare il driver di InternetExplorer: " + e.getMessage());
					}
				else
					driver = new InternetExplorerDriver();
				break;

			case EDGE:
				String pathEdge = config.pathEdge();
				validateWebDriverPath(WEBDRIVER_PATH_EDGE, pathEdge);
				match = isRemoteRegex.matcher(pathEdge);
				isRemote = match.matches();
				if (isRemote)
					try {
						URL url = new URL(match.group(1));
						log.info("Using remote driver, URL {}", url);
						DesiredCapabilities capabilities = DesiredCapabilities.edge();
						// ADD CAPABILITIES HERE...
						driver = new RemoteWebDriver(url, capabilities);
					} catch (MalformedURLException e) {
						throw new RuntimeException("Impossibile caricare il driver di Edge: " + e.getMessage());
					}
				else
					driver = new EdgeDriver();
				break;

			default:
				// qui non dovvrebbe mai arrivare perchè lo switch lavora su una enumeration (dunque prima fallisce il
				// metodo valueOf() per inizializzare la var browser)
				throw new IllegalStateException("No driver matched");
			}

			/*
			 * Timeouts
			 */
			Double findElementTimeout = config.timeoutFindElement();
			Assert.notNull(findElementTimeout, "Missing '" + WEBDRIVER_TIMEOUTS_FIND + "' property");
			Assert.isTrue(findElementTimeout > 0, "Invalid '" + WEBDRIVER_TIMEOUTS_FIND + "' property");
			driver.manage().timeouts().implicitlyWait((long) (findElementTimeout * 1000), TimeUnit.MILLISECONDS);

			Double pageLoadTimeout = config.timeoutPageLoad();
			Assert.notNull(pageLoadTimeout, "Missing '" + WEBDRIVER_TIMEOUTS_LOAD + "' property");
			Assert.isTrue(pageLoadTimeout > 0, "Invalid '" + WEBDRIVER_TIMEOUTS_LOAD + "' property");
			driver.manage().timeouts().pageLoadTimeout((long) (pageLoadTimeout * 1000), TimeUnit.MILLISECONDS);

			Double scriptTimeout = config.timeoutScript();
			Assert.notNull(scriptTimeout, "Missing '" + WEBDRIVER_TIMEOUTS_SCRIPT + "' property");
			Assert.isTrue(scriptTimeout > 0, "Invalid '" + WEBDRIVER_TIMEOUTS_SCRIPT + "' property");
			driver.manage().timeouts().setScriptTimeout((long) (scriptTimeout * 1000), TimeUnit.MILLISECONDS);

			/*
			 * Dimensione della finestra del browser
			 */

			String windowPos = config.windowPos();
			if (StringUtils.hasText(windowPos))
				if ("maximize".equalsIgnoreCase(windowPos)) {

					driver.manage().window().maximize();
					log.debug("Set browser in FULLSCREEN mode");

				} else {

					String[] coord = windowPos.split(",");
					Assert.isTrue(coord.length == 4, "Invalid '" + WEBDRIVER_WINDOW_POSITION + "' property: '" + windowPos + "' ");

					int width = Integer.parseInt(coord[2]);
					int height = Integer.parseInt(coord[3]);
					driver.manage().window().setSize(new Dimension(width, height));

					int x = Integer.parseInt(coord[0]);
					int y = Integer.parseInt(coord[1]);
					driver.manage().window().setPosition(new Point(x, y));
				}

		} catch (Throwable e) {
			throw new SeleniumSetupException("Failed to initialize WebDriver! " + e.getMessage());
		}
	}

	/*
	 * Private methods
	 */

	/**
	 * Si occupa di settare correttamente il path del webdriver, eventualmente lanciando una eccezione se ci sono
	 * errori.
	 * 
	 * @param pathSysProp
	 *            nome della SystemProperty
	 * @param path
	 *            path letto dalla configurazione
	 * @throws IllegalArgumentException
	 *             eccezione sollevata se il path letto dalla configurazione è vuoto e manca anche la SystemProperty
	 */
	private void validateWebDriverPath(String pathSysProp, String path) throws IllegalArgumentException {
		/*
		 * Se il path è vuoto allora sollevo eccezione
		 */
		Assert.hasText(path, "Missing '" + pathSysProp + "' property");
		System.setProperty(pathSysProp, path);
	}

}
