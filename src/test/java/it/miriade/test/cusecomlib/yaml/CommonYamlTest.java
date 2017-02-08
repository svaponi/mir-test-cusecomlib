package it.miriade.test.cusecomlib.yaml;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.miriade.test.cusecomlib.CuseSpringConfiguration;
import it.miriade.test.cusecomlib.CuseUtil;
import it.miriade.test.cusecomlib.cucumber.stepdefs.Stepdef;

/**
 * Test estrazioni dati dallo yaml comune
 * 
 * @author svaponi
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CuseSpringConfiguration.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CommonYamlTest implements Stepdef {

	static final String PRODUCT_NAME = "TESTAMI";
	static final String PRODUCT_FULLNAME = "Il nuovo prodotto da testare";

	@Autowired
	private CuseUtil util;

	@Test
	public void t00() {
		Assert.assertNotNull("util is null", util);
		Assert.assertNotNull("util.commonYaml() is null", util.commonYaml());
	}

	@Test
	public void t01() {
		String productName = util.commonYaml().getString("product_name");
		Assert.assertEquals("commonYaml is not equal", PRODUCT_NAME, productName);
		String productFullname = util.commonYaml().getString("product_fullname");
		Assert.assertEquals("commonYaml is not equal", PRODUCT_FULLNAME, productFullname);
	}

}
