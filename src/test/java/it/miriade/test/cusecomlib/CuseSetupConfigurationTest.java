package it.miriade.test.cusecomlib;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.miriade.test.cusecomlib.CuseSetupConfiguration;
import it.miriade.test.cusecomlib.CuseSpringConfiguration;
import it.miriade.test.cusecomlib.cucumber.stepdefs.Stepdef;

/**
 * Unit test di {@link CuseSetupConfiguration}
 * 
 * @author svaponi
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CuseSpringConfiguration.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CuseSetupConfigurationTest implements Stepdef {

	@Autowired
	private CuseSetupConfiguration config;

	@Test
	public void t00_DependencyInjectionTest() {
		Assert.assertNotNull("config is null", config);
	}

}
