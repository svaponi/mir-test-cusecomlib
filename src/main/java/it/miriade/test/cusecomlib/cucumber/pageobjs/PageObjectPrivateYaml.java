package it.miriade.test.cusecomlib.cucumber.pageobjs;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import it.miriade.test.cusecomlib.CuseDefaultSpec;
import it.miriade.test.cusecomlib.yaml.YamlSupport;
import it.miriade.test.cusecomlib.yaml.YamlSupportFactory;

public abstract class PageObjectPrivateYaml implements PageObject, InitializingBean {

	/**
	 * {@link YamlSupport} per il file YAML privato della classe. E' collocato al basepath degli yaml (vedi
	 * {@link CuseDefaultSpec#YAML_SUPPORT_CLASSPATH_DIR}) e avrà lo stesso nome della classe che stende
	 * {@link PageObjectPrivateYaml} e con estensione .yml
	 */
	protected YamlSupport privateYml;

	@Autowired
	private YamlSupportFactory yamlSupportFactory;

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(yamlSupportFactory);
		privateYml = yamlSupportFactory.build(getClass());
	}

}