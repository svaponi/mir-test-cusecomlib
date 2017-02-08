package it.miriade.test.cusecomlib.yaml;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;

import it.miriade.test.cusecomlib.excep.YamlInvalidKeyException;
import it.miriade.test.cusecomlib.yaml.YamlSupport;

/**
 * Test per le funzionalità di {@link YamlSupport}
 * 
 * @author svaponi
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class YamlSupportTest {

	/**
	 * Directory base dei file YAML usati nel test
	 */
	static final String TEST_RESOURCE_DIR = YamlSupportTest.class.getSimpleName().concat("Resources");
	static final String TEST_YAML_DATA_FILE = TEST_RESOURCE_DIR + "/data.yml";
	static final String TEST_YAML_ARRAY_FILE = TEST_RESOURCE_DIR + "/array.yml";
	static final List<String> TEST_YAML_CONF_FILES = Arrays.asList(TEST_RESOURCE_DIR + "/conf-chrome.yml", TEST_RESOURCE_DIR + "/conf-firefox.yml", TEST_RESOURCE_DIR + "/conf-ie.yml");

	static YamlSupport dataYml;
	static YamlSupport arrayYmlNullOnInvalidKey;
	static YamlSupport arrayYmlThrowOnInvalidKey;

	@BeforeClass
	public static void setup() {
		arrayYmlThrowOnInvalidKey = new YamlSupport(TEST_YAML_ARRAY_FILE, true);
		arrayYmlNullOnInvalidKey = new YamlSupport(TEST_YAML_ARRAY_FILE, false);
		dataYml = new YamlSupport(TEST_YAML_DATA_FILE);
	}

	@Test
	public void t01_read_config() {
		// System.out.println("t01_read_config");

		for (String YAML_FILE : TEST_YAML_CONF_FILES) {
			YamlSupport configYml = new YamlSupport(YAML_FILE);

			Assert.assertEquals("DEBG", configYml.get("GlobalConf.LogMode"));
			Assert.assertEquals("5667", configYml.get("outputInfo.nscaInfo.NSCAport"));
			Assert.assertEquals("192.168.254.20", configYml.get("outputInfo.nscaInfo.NSCAserver"));
			Assert.assertEquals("f:/Home/Lavoro/Progetti/Github/cucumber/Cfg/send_nsca.cfg", configYml.get("outputInfo.nscaInfo.NSCAconfigFile"));
			Assert.assertEquals(false, configYml.get("outputInfo.mailInfo.mailEnable"));
			Assert.assertEquals("sergio@bosoconsulting.it", configYml.get("outputInfo.mailInfo.mailToAddress"));
			Assert.assertEquals(587, configYml.get("outputInfo.mailInfo.mailPort"));
		}

		// System.out.println("t01_read_config > OK");
	}

	@Test
	public void t02_read_data() {
		// System.out.println("t02_read_data");

		Assert.assertEquals("test", dataYml.get("target_environment"));
		Assert.assertEquals("Oggetto della mail", dataYml.get("templates.mails.?.subject", "EXAMPLE"));
		Assert.assertEquals("lmen176-02@testmail.org", dataYml.get("test.users.?.mailboxes[1]", "TEST_USER"));
		Assert.assertEquals("startswith", dataYml.get("test.filters.?.conditions[1].type", "EXAMPLE"));
		Assert.assertEquals("cc", dataYml.get("test.filters.?.conditions[2].field", "EXAMPLE"));
		Assert.assertEquals("testautolmen02@testmail.org > warning", dataYml.get("test.filters.?.action_to_value", "F2CP"));
		Assert.assertEquals("samuel", dataYml.getString("test.users.?.userid", "TEST_USER"));
		Assert.assertEquals("samuel123", dataYml.getString("test.users.?.password", "TEST_USER"));

		// System.out.println("t02_read_data > OK");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void t03_read_array() {
		// System.out.println("t03_read_array");

		Assert.assertEquals("value4", arrayYmlNullOnInvalidKey.get("key1.array2[0].key3.key4"));
		Assert.assertEquals("value4B", arrayYmlNullOnInvalidKey.get("key1.array2[1].key3.array4[1]"));
		Assert.assertEquals("value4F", arrayYmlNullOnInvalidKey.get("key1.array2[2].key3.array4[5]"));
		Object array4 = arrayYmlNullOnInvalidKey.get("key1.array2[2].key3.array4");
		Assert.assertTrue("Array returned with class List", List.class.isAssignableFrom(array4.getClass()));
		int letter = 'A';
		for (String item : ((List<String>) array4))
			Assert.assertEquals("Array items match", item, "value4" + ((char) letter++));

		// System.out.println("t03_read_array > OK");
	}

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void t04_missing_key() {
		// System.out.println("t04_missing_key");

		exception.expect(YamlInvalidKeyException.class);
		Assert.assertNull("Missing key", arrayYmlThrowOnInvalidKey.get("key1.array2[0].key3.key5"));

		// System.out.println("t04_missing_key > OK");
	}

	@Test
	public void t05_missing_key_2() {
		// System.out.println("t05_missing_key_2");

		exception.expect(YamlInvalidKeyException.class);
		Assert.assertNull("Missing key", arrayYmlThrowOnInvalidKey.get("key1.array2[0].key9.key1"));

		// System.out.println("t05_missing_key_2 > OK");
	}

	@Test
	public void t06_array_out_of_index() {
		// System.out.println("t06_array_out_of_index");

		exception.expect(YamlInvalidKeyException.class);
		Assert.assertNull("Array out of index", arrayYmlThrowOnInvalidKey.get("key1.array2[5].key3.key5"));

		// System.out.println("t06_array_out_of_index > OK");
	}

	@Test
	public void t07_invalid_key() {
		// System.out.println("t07_invalid_key");

		exception.expect(YamlInvalidKeyException.class);
		Assert.assertNull("Missing intermediate key", arrayYmlThrowOnInvalidKey.get("key1.array9[0].key3.key5"));

		// System.out.println("t07_invalid_key > OK");
	}

	@Test
	public void t08_return_null() {
		// System.out.println("t08_return_null");

		Assert.assertNull("Missing key", arrayYmlNullOnInvalidKey.get("key1.array2[0].key3.key5"));
		Assert.assertNull("Missing key", arrayYmlNullOnInvalidKey.get("key1.array2[0].key9.key1"));
		Assert.assertNull("Array out of index", arrayYmlNullOnInvalidKey.get("key1.array2[5].key3.key5"));
		Assert.assertNull("Missing intermediate key", arrayYmlNullOnInvalidKey.get("key1.array9[0].key3.key5"));

		// System.out.println("t08_return_null > OK");
	}
}
