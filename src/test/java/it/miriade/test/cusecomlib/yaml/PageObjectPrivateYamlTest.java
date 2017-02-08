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
import it.miriade.test.cusecomlib.yaml.YamlSupportFactory;

/**
 * Test per le funzionalità di {@link YamlSupportFactory}. Per testarne le funzionalità utilizzo un PageObject di prova,
 * {@link MyPageObject}.
 * 
 * @author svaponi
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CuseSpringConfiguration.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PageObjectPrivateYamlTest {

	@Autowired
	private MyPageObject myPage;

	@Test
	public void t01_detect_caller() {

		Assert.assertNotNull("Problemi con @Autowired MyPageObjectImpl", myPage);

		Assert.assertNotNull("YamlSupport è null", myPage.getYml());

		String yamlName = YamlSupportFactory.getYamlNameByClass(MyPageObject.class);

		Assert.assertEquals("YamlSupport non è quello giusto", yamlName, myPage.getYml().getYamlName());

	}

}
