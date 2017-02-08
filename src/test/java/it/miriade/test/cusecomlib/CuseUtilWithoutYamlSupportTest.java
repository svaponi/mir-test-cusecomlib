package it.miriade.test.cusecomlib;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import it.miriade.test.cusecomlib.CuseSetupConfiguration;
import it.miriade.test.cusecomlib.CuseUtil;

/**
 * Unit test di {@link CuseUtil} senza YAML Support.
 * 
 * @author svaponi
 */
public class CuseUtilWithoutYamlSupportTest {

	private static CuseUtil util;

	/*
	 * Costruisco un CuseUtil senza YAML Support
	 */
	@BeforeClass
	public static void afterPropertiesSet() {
		CuseSetupConfiguration config = new CuseSetupConfiguration();
		Assert.assertNotNull("config is null", config);
		util = new CuseUtil(config);
		Assert.assertNotNull("util is null", util);
	}

	@Rule
	public final ExpectedException exception = ExpectedException.none();
	public final Class<? extends Exception> exceptionClass = UnsupportedOperationException.class;

	@Test
	public void t01() {
		exception.expect(exceptionClass);
		util.findBy("pinco");
	}

	@Test
	public void t02() {
		exception.expect(exceptionClass);
		util.clickBy("pinco");
	}

	@Test
	public void t03() {
		exception.expect(exceptionClass);
		util.existsBy("pinco");
	}

	@Test
	public void t04() {
		exception.expect(exceptionClass);
		util.setTextBy("pinco", "pallo");
	}

}
