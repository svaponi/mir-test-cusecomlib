package cucumber.miriade.stepdefs;

import static org.junit.Assert.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java8.En;
import it.miriade.test.cusecomlib.CuseUtil;
import it.miriade.test.cusecomlib.cucumber.stepdefs.Stepdef;
import it.miriade.test.cusecomlib.enums.BySelector;

/**
 * Feature ausiliari di natura generale, non accoppiati al progetto. Riutilizzabili.
 * 
 * @author svaponi
 */
public class CommonJava8Stepdef implements Stepdef, En {

	@Autowired
	private CuseUtil util;

	// Esempio di test con Java8
	// ==============================================================================

	public CommonJava8Stepdef() {

		// Navigazione
		When("^I navigate to \"([^\"]*)\" Java8$", (String baseUrl) -> {
			util.driver().navigate().to(baseUrl);
		});
		When("^I navigate to yaml:\"([^\"]*)\" Java8$", (String key) -> {
			String baseUrl = util.targetEnvYaml().getString(key);
			util.driver().get(baseUrl);
		});
		When("^I navigate back Java8$", () -> {
			util.driver().navigate().back();
		});
		When("^I navigate forward Java8$", () -> {
			util.driver().navigate().forward();
		});

		// Test basilari sulla pagina
		Then("^the title should match \"([^\"]*)\" Java8$", (String title) -> {
			assertTrue(util.driver().getTitle().equals(title));
		});
		Then("^the title should match yaml:\"([^\"]*)\" Java8$", (String key) -> {
			String title = util.targetEnvYaml().getString(key);
			assertTrue(util.driver().getTitle().equals(title));
		});
		Then("^the title should contain \"([^\"]*)\" Java8$", (String title) -> {
			assertTrue(util.driver().getTitle().contains(title));
		});
		Then("^the title should contain yaml:\"([^\"]*)\" Java8$", (String key) -> {
			String title = util.targetEnvYaml().getString(key);
			assertTrue(util.driver().getTitle().contains(title));
		});

		// Click
		When("^I click on id \"([^\"]*)\" Java8$", (String id) -> {
			util.clickBy(BySelector.ID, id);
		});
		When("^I click on xpath \"([^\"]*)\" Java8$", (String xpath) -> {
			util.clickBy(BySelector.XPATH, xpath);
		});
		When("^I click on \"([^\"]*)\" => \"([^\"]*)\" Java8$", (String by, String expr) -> {
			util.clickBy(by, expr);
		});

		// Write
		When("^I write \"([^\"]*)\" on id \"([^\"]*)\" Java8$", (String id, String value) -> {
			util.setTextBy(BySelector.ID, id, value);
		});
		When("^I write \"([^\"]*)\" on xpath \"([^\"]*)\" Java8$", (String xpath, String value) -> {
			util.setTextBy(BySelector.XPATH, xpath, value);
		});
		When("^I write \"([^\"]*)\" on \"([^\"]*)\" => \"([^\"]*)\" Java8$", (String by, String expr, String value) -> {
			util.setTextBy(by, expr, value);
		});

		// Wait
		And("^I wait (\\d+) s Java8$", (Long seconds) -> {
			util.waitMillis(seconds * 1000);
		});
		And("^I wait (\\d+) ms Java8$", (Long millis) -> {
			util.waitMillis(millis);
		});

		// Debug
		// ========================================================

		/**
		 * Fa partire la console e rimene in ascolto sullo STDIN. Permette di provare le coppie {@link BySelector} +
		 * expression.
		 */
		And("^debug Java8$", () -> {
			util.debug();
		});

		/**
		 * Tentivo di emulare la console interattiva pry di Ruby.
		 * <br/>
		 * Vedi <a href="https://github.com/pry/pry">https://github.com/pry/pry</a>
		 */
		And("^pry Java8$", () -> {
			throw new UnsupportedOperationException("Not implemented");
		});

		/*
		 * Per aggiungere la console Java è necessario aggiungere una libreria per la compilazione di sorgente a runtime
		 */
		And("^console Java8$", () -> {
			throw new UnsupportedOperationException("Not implemented");
		});

	}

}
