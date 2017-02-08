package it.miriade.test.cusecomlib.hooks;

import javax.annotation.PostConstruct;

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
import it.miriade.test.cusecomlib.CuseSpringConfiguration;
import it.miriade.test.cusecomlib.excep.YamlInvalidKeyException;
import it.miriade.test.cusecomlib.hooks.HtmlHookFactory;
import it.miriade.test.cusecomlib.yaml.YamlSupportFactory;

/**
 * Unit test di {@link HtmlHookFactory}
 * 
 * @author svaponi
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CuseSpringConfiguration.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HtmlHookFactoryTest {

	@Autowired
	private YamlSupportFactory yamlSupportFactory;

	private HtmlHookFactory hookFactory;

	@PostConstruct
	public void initMethod() {
		Assert.assertNotNull(yamlSupportFactory);
		hookFactory = new HtmlHookFactory(yamlSupportFactory.build(CuseDefaultSpec.YAML_SUPPORT_COMMON_FILENAME));
	}

	@Test
	public void t00() {
		Assert.assertNotNull(hookFactory);
	}

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void t01() {
		exception.expect(YamlInvalidKeyException.class);
		hookFactory.build("pinco.pallo");
	}
}