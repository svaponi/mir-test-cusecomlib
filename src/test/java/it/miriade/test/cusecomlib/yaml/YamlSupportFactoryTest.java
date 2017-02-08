package it.miriade.test.cusecomlib.yaml;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.miriade.test.cusecomlib.CuseDefaultSpec;
import it.miriade.test.cusecomlib.CuseSetupConfiguration;
import it.miriade.test.cusecomlib.CuseSpringConfiguration;
import it.miriade.test.cusecomlib.yaml.YamlSupport;
import it.miriade.test.cusecomlib.yaml.YamlSupportFactory;

/**
 * Test per le funzionalità di {@link YamlSupportFactory}. Per testarne le funzionalità utilizzo un PageObject di prova.
 * 
 * @see MyPageObject
 * @author svaponi
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CuseSpringConfiguration.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class YamlSupportFactoryTest {

	@Autowired
	private YamlSupportFactory factory;

	@Autowired
	private CuseSetupConfiguration config;

	@Test
	public void t00_factoy() {

		Assert.assertNotNull("factory is null", factory);
		Assert.assertNotNull("config is null", config);

		/*
		 * Qui voglio testare che la factory mi restituisca sempre lo stesso YamlSupport se l'input è uguale
		 */
		YamlSupport commonYaml = factory.getCommonYaml();
		Assert.assertNotNull("commonYaml is null", commonYaml);
		Assert.assertEquals("YamlSupport(common) is not equal", config.yamlClasspathDir() + CuseDefaultSpec.YAML_SUPPORT_COMMON_FILENAME + ".yml", commonYaml.getYamlPath());

		YamlSupport yaml = factory.build(CuseDefaultSpec.YAML_SUPPORT_COMMON_FILENAME);
		Assert.assertNotNull("YamlSupport(" + CuseDefaultSpec.YAML_SUPPORT_COMMON_FILENAME + ") is null", yaml);
		Assert.assertEquals("YamlSupport non è quello giusto", commonYaml.getYamlName(), yaml.getYamlName());
		Assert.assertEquals("YamlSupport non è quello giusto", commonYaml.getYamlPath(), yaml.getYamlPath());
		Assert.assertEquals("YamlSupport non è quello giusto", commonYaml, yaml);

	}

}
