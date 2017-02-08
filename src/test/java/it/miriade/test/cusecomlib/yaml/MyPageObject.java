package it.miriade.test.cusecomlib.yaml;

import org.springframework.stereotype.Component;

import it.miriade.test.cusecomlib.cucumber.pageobjs.PageObjectPrivateYaml;
import it.miriade.test.cusecomlib.yaml.YamlSupport;

/**
 * Page object di prova per testare l'ereditarietà dello yaml privato di {@link PageObjectPrivateYaml}
 * 
 * @see PageObjectPrivateYaml
 * @author svaponi
 */
@Component
public class MyPageObject extends PageObjectPrivateYaml {

	public YamlSupport getYml() {
		return privateYml;
	}

}