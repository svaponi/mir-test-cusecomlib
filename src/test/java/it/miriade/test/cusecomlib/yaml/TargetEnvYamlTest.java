package it.miriade.test.cusecomlib.yaml;

import java.util.List;

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
 * Test estrazioni dati del'ambiente
 * 
 * @author svaponi
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CuseSpringConfiguration.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TargetEnvYamlTest implements Stepdef {

	static final String target_environment = "test";

	// dati di prova per simulare unnambiente di test
	static final String test_url = "http://www.miriade.it";
	static final String test_title = "Miriade - play the change, change the play";

	// dati di prova per simulare unnambiente di collaudo
	static final String collaudo_url = "http://www.miriade.it/big-data/";
	static final String collaudo_title = "Big Data | Miriade";

	@Autowired
	private CuseUtil commonlib;

	public boolean isTest() {
		return commonlib.config().targetEnv().equalsIgnoreCase("test");
	}

	@Test
	public void t00() {
		Assert.assertNotNull("commonlib is null", commonlib);
		Assert.assertNotNull("commonlib.targetEnvYaml() is null", commonlib.targetEnvYaml());
	}

	@Test
	public void t01() {
		String baseUrl = commonlib.targetEnvYaml().getString("url");
		Assert.assertNotNull("baseUrl is null", baseUrl);
		Assert.assertEquals("baseUrl is not equal", isTest() ? test_url : collaudo_url, baseUrl);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void t02_get() {
		Assert.assertEquals((isTest() ? "ts-" : "cl-") + "value4", commonlib.targetEnvYaml().get("key1.array2[0].key3.key4"));
		Assert.assertEquals((isTest() ? "ts-" : "cl-") + "value4B", commonlib.targetEnvYaml().get("key1.array2[1].key3.array4[1]"));
		Assert.assertEquals((isTest() ? "ts-" : "cl-") + "value4F", commonlib.targetEnvYaml().get("key1.array2[2].key3.array4[5]"));
		Object array4 = commonlib.targetEnvYaml().get("key1.array2[2].key3.array4");
		Assert.assertTrue("Array returned with class List", List.class.isAssignableFrom(array4.getClass()));
		int letter = 'A';
		for (String item : ((List<String>) array4))
			Assert.assertEquals("Array items match", item, (isTest() ? "ts-" : "cl-") + "value4" + ((char) letter++));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void t03_get_varargs() {
		Assert.assertEquals((isTest() ? "ts-" : "cl-") + "value4", commonlib.targetEnvYaml().get("key1.array2[0].?.key4", "key3"));
		Assert.assertEquals((isTest() ? "ts-" : "cl-") + "value4B", commonlib.targetEnvYaml().get("key1.?.key3.array4[1]", "array2[1]"));
		Assert.assertEquals((isTest() ? "ts-" : "cl-") + "value4F", commonlib.targetEnvYaml().get("key1.?[?].key3.array4[5]", "array2", "2"));
		Assert.assertEquals((isTest() ? "ts-" : "cl-") + "value4B", commonlib.targetEnvYaml().get("key1.?[?].key?.array?[?]", "array2", "2", "3", "4", "1"));
		Object array4 = commonlib.targetEnvYaml().get("key1.?[?].key?.array?", "array2", "2", "3", "4");
		Assert.assertTrue("Array returned with class List", List.class.isAssignableFrom(array4.getClass()));
		int letter = 'A';
		for (String item : ((List<String>) array4))
			Assert.assertEquals("Array items match", item, (isTest() ? "ts-" : "cl-") + "value4" + ((char) letter++));
	}
}
