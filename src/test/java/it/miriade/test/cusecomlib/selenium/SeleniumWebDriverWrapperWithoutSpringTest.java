package it.miriade.test.cusecomlib.selenium;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.WebDriver;

import it.miriade.test.cusecomlib.CuseDefaultSpec;
import it.miriade.test.cusecomlib.CuseSetupConfiguration;
import it.miriade.test.cusecomlib.enums.Browser;
import it.miriade.test.cusecomlib.excep.SeleniumSetupException;
import it.miriade.test.cusecomlib.utils.SetupUtil;

/**
 * Testiamo il wrapper senza Spring
 * 
 * @author svaponi
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SeleniumWebDriverWrapperWithoutSpringTest {

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

		try (SeleniumWebDriverWrapper wrapper = new SeleniumWebDriverWrapper(config)) {
			Assert.assertNotNull(wrapper);

			wrapper.setup();
			Assert.assertNotNull(wrapper.get());
			Assert.assertEquals(Browser.CHROME, wrapper.getBrowser());
			Assert.assertFalse(wrapper.isRemote());

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

		try (SeleniumWebDriverWrapper wrapper = new SeleniumWebDriverWrapper(config)) {
			Assert.assertNotNull(wrapper);

			wrapper.setup();
			Assert.assertNotNull(wrapper.get());
			Assert.assertEquals(Browser.CHROME, wrapper.getBrowser());
			Assert.assertTrue(wrapper.isRemote());
		}
	}

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	/*
	 * exception.expectMessage() verifica SOLO se il testo è CONTENUTO nel messaggio della eccezione, non se è uguale
	 */

	@Test
	public void t01_null_setup_configuration() {

		SeleniumWebDriverWrapper wrapper = new SeleniumWebDriverWrapper(null);
		Assert.assertNotNull(wrapper);

		exception.expect(SeleniumSetupException.class);
		exception.expectMessage("Missing setup configuration"); // verifica se il testo è contenuto nel msg della ex
		wrapper.setup();
	}

	@Test
	public void t02_missing_chrome_path() {

		CuseSetupConfiguration config = new CuseSetupConfiguration();
		// config.setPathChrome(null);
		Assert.assertNotNull(config);

		SeleniumWebDriverWrapper wrapper = new SeleniumWebDriverWrapper(config);
		Assert.assertNotNull(wrapper);

		exception.expect(SeleniumSetupException.class);
		exception.expectMessage("Failed to initialize WebDriver! Missing '" + CuseDefaultSpec.WEBDRIVER_PATH_CHROME + "' property");
		wrapper.setup();
	}

	@Test
	public void t03_missing_firefox_path() {

		CuseSetupConfiguration config = new CuseSetupConfiguration();
		config.targetBrowser(Browser.FIREFOX);
		// config.setPathFirefox(null);
		Assert.assertNotNull(config);

		SeleniumWebDriverWrapper wrapper = new SeleniumWebDriverWrapper(config);
		Assert.assertNotNull(wrapper);

		exception.expect(SeleniumSetupException.class);
		exception.expectMessage("Failed to initialize WebDriver! Missing '" + CuseDefaultSpec.WEBDRIVER_PATH_FIREFOX + "' property");
		wrapper.setup();
	}
}
