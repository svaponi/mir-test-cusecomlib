package it.miriade.test.cusecomlib.selenium;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.miriade.test.cusecomlib.CuseDefaultSpec;
import it.miriade.test.cusecomlib.CuseSetupConfiguration;
import it.miriade.test.cusecomlib.CuseSpringConfiguration;
import it.miriade.test.cusecomlib.excep.SeleniumSetupException;
import it.miriade.test.cusecomlib.selenium.SeleniumWebDriverWrapper;

/**
 * Unit test di {@link SeleniumWebDriverWrapper}.
 * 
 * @author svaponi
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CuseSpringConfiguration.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SeleniumWebDriverWrapperTest {

	@Autowired
	private CuseSetupConfiguration config;

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void t00() {
		Assert.assertNotNull("config is null", config);
		SeleniumWebDriverWrapper wrapper = new SeleniumWebDriverWrapper(config);
		Assert.assertNotNull("wrapper is null", wrapper);

		/*
		 * Se chiedo il WebDriver prima del setup ritorna eccezione
		 */
		exception.expect(SeleniumSetupException.class);
		exception.expectMessage("Uninitialized WebDriver"); // verifica se il testo è contenuto nel msg della ex
		wrapper.get();
	}

	@Test
	public void t01() {

		if (System.getProperty(CuseDefaultSpec.RUN_INTEGRATION_TESTS) == null)
			return;

		Assert.assertNotNull("config is null", config);
		SeleniumWebDriverWrapper wrapper = new SeleniumWebDriverWrapper(config);
		Assert.assertNotNull("wrapper is null", wrapper);

		wrapper.setup();
		Assert.assertNotNull(wrapper.get());

		wrapper.quit();
	}

}
