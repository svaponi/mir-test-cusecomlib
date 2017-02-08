package it.miriade.test.cusecomlib;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.WebDriver;

import it.miriade.test.cusecomlib.enums.Browser;
import it.miriade.test.cusecomlib.excep.SeleniumSetupException;
import it.miriade.test.cusecomlib.utils.SetupUtil;

/**
 * Testiamo il wrapper senza Spring
 * 
 * @author svaponi
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CuseUtilWithoutSpringTest {

	// cartella con le risorse del test
	static final String TEST_RESOURCE_DIR = WebDriver.class.getSimpleName().concat("Resources");

	@Test
	public void t00_init_local_driver() {

		if (System.getProperty(CuseDefaultSpec.RUN_INTEGRATION_TESTS) == null)
			return;

		CuseSetupConfiguration config = SetupUtil.buildCondigurationFromProperties("src/test/resources/" + TEST_RESOURCE_DIR + "/local-driver.properties");
		Assert.assertNotNull(config);

		config.targetBrowser(Browser.CHROME);
		Assert.assertEquals("CHROME", config.targetBrowser());

		try (CuseUtil util = new CuseUtil(config)) {
			Assert.assertNotNull(util);
			Assert.assertNotNull(util.driver()); // al primo driver() viene inizializzato il driver
			Assert.assertNotNull(util.driverWrapper());
			Assert.assertEquals(Browser.CHROME, util.driverWrapper().getBrowser());
			Assert.assertFalse(util.driverWrapper().isRemote());
			String stepdefName = CuseUtil.getStepdefName();
			util.loadPage("https://www.google-it/#q=" + stepdefName);
			util.currentUrl().endsWith(stepdefName);
			util.currentAngularPath().equalsIgnoreCase("q=" + stepdefName);
		}
	}

	// missing remote driver
	// @Test
	public void t00_init_remote_driver() {

		if (System.getProperty(CuseDefaultSpec.RUN_INTEGRATION_TESTS) == null)
			return;

		CuseSetupConfiguration config = SetupUtil.buildCondigurationFromProperties("src/test/resources/" + TEST_RESOURCE_DIR + "/remote-driver.properties");
		Assert.assertNotNull(config);

		config.targetBrowser(Browser.CHROME);
		Assert.assertEquals("CHROME", config.targetBrowser());

		try (CuseUtil util = new CuseUtil(config)) {
			Assert.assertNotNull(util);
			Assert.assertNotNull(util.driver()); // al primo driver() viene inizializzato il driver
			Assert.assertNotNull(util.driverWrapper());
			Assert.assertEquals(Browser.CHROME, util.driverWrapper().getBrowser());
			Assert.assertTrue(util.driverWrapper().isRemote());
			String stepdefName = CuseUtil.getStepdefName();
			util.loadPage("https://www.google-it/#q=" + stepdefName);
			util.currentUrl().endsWith(stepdefName);
			util.currentAngularPath().equalsIgnoreCase("q=" + stepdefName);
		}
	}

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	/*
	 * exception.expectMessage() verifica SOLO se il testo è CONTENUTO nel messaggio della eccezione, non se è uguale
	 */

	@Test
	public void t01_null_setup_configuration() {

		CuseUtil seleniumUtil = new CuseUtil(null);
		Assert.assertNotNull(seleniumUtil);

		exception.expect(SeleniumSetupException.class);
		exception.expectMessage("Missing setup configuration"); // verifica se il testo è contenuto nel msg della ex
		seleniumUtil.driver(); // al primo driver() viene inizializzato il driver
	}

	@Test
	public void t02_missing_chrome_path() {

		CuseSetupConfiguration config = new CuseSetupConfiguration();
		// config.setPathChrome("");
		Assert.assertNotNull(config);

		CuseUtil seleniumUtil = new CuseUtil(config);
		Assert.assertNotNull(seleniumUtil);

		exception.expect(SeleniumSetupException.class);
		exception.expectMessage("Failed to initialize WebDriver! Missing '" + CuseDefaultSpec.WEBDRIVER_PATH_CHROME + "' property");
		seleniumUtil.driver(); // al primo driver() viene inizializzato il driver
	}

	@Test
	public void t03_missing_firefox_path() {

		CuseSetupConfiguration config = new CuseSetupConfiguration();
		config.targetBrowser(Browser.FIREFOX);
		// config.setPathFirefox("");
		Assert.assertNotNull(config);

		CuseUtil seleniumUtil = new CuseUtil(config);
		Assert.assertNotNull(seleniumUtil);

		exception.expect(SeleniumSetupException.class);
		exception.expectMessage("Failed to initialize WebDriver! Missing '" + CuseDefaultSpec.WEBDRIVER_PATH_FIREFOX + "' property");
		seleniumUtil.driver(); // al primo driver() viene inizializzato il driver
	}
}
