package it.miriade.test.cusecomlib.cucumber;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.miriade.test.cusecomlib.CuseDefaultSpec;
import it.miriade.test.cusecomlib.CuseSpringConfiguration;
import it.miriade.test.cusecomlib.cucumber.CucumberRunner;

/**
 * Lancio il {@link CucumberRunner#run()}
 * 
 * @author svaponi
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CuseSpringConfiguration.class)
public class CucumberRunnerTest {

	@Autowired
	private CucumberRunner cucumber;

	@Test
	public void test() throws Throwable {
		if (System.getProperty(CuseDefaultSpec.RUN_INTEGRATION_TESTS) == null)
			return;
		byte exitstatus = cucumber.run();
		if (exitstatus != CucumberRunner.success)
			throw new RuntimeException("Cucumber terminated with errors");
	}
}
