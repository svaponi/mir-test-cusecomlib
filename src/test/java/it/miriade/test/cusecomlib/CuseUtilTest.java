package it.miriade.test.cusecomlib;

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

import it.miriade.test.cusecomlib.CuseSpringConfiguration;
import it.miriade.test.cusecomlib.CuseUtil;
import it.miriade.test.cusecomlib.excep.YamlInvalidKeyException;

/**
 * Unit test di {@link CuseUtil}.
 * 
 * @author svaponi
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CuseSpringConfiguration.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CuseUtilTest {

	// TODO: trovare un modo per testare le funzionalità. Esempio utilizzando una pagina web locale in modo da poter
	// mantenere i tesst nel tempo.

	@Autowired
	private CuseUtil util;

	@Test
	public void t00() {
		Assert.assertNotNull("util is null", util);
	}

	@Rule
	public final ExpectedException exception = ExpectedException.none();
	public final Class<? extends Exception> exceptionClass = YamlInvalidKeyException.class;

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
