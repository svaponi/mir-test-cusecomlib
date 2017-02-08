package cucumber.miriade.stepdefs;

import static org.junit.Assert.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import it.miriade.test.cusecomlib.CuseUtil;
import it.miriade.test.cusecomlib.cucumber.stepdefs.Stepdef;
import it.miriade.test.cusecomlib.enums.BySelector;

/**
 * Feature ausiliari di natura generale, non accoppiati al progetto. Riutilizzabili.
 * 
 * @author svaponi
 */
public class CommonStepdef implements Stepdef {

	@Autowired
	private CuseUtil util;

	// Navigazione
	// ==============================================================================

	@When("^I navigate to \"([^\"]*)\"$")
	public void I_navigate_to(String baseUrl) {
		util.driver().navigate().to(baseUrl);
	}

	@When("^I navigate to yaml:\"([^\"]*)\"$")
	public void I_navigate_to_yaml(String key) {
		String baseUrl = util.targetEnvYaml().getString(key);
		util.driver().get(baseUrl);
	}

	@When("^I navigate back$")
	public void I_navigate_back() {
		util.driver().navigate().back();
	}

	@When("^I navigate forward$")
	public void I_navigate_forward() {
		util.driver().navigate().forward();
	}

	// Test basilari sulla pagina
	// ==============================================================================

	@Then("^the title should match \"([^\"]*)\"$")
	public void the_title_should_match(String title) throws Throwable {
		assertTrue(util.driver().getTitle().equals(title));
	}

	@Then("^the title should contain \"([^\"]*)\"$")
	public void the_title_should_contain(String title) throws Throwable {
		assertTrue(util.driver().getTitle().contains(title));
	}

	@Then("^the title should match yaml:\"([^\"]*)\"$")
	public void the_title_should_match_yaml(String key) throws Throwable {
		String title = util.targetEnvYaml().getString(key);
		assertTrue(util.driver().getTitle().equals(title));
	}

	@Then("^the title should contain yaml:\"([^\"]*)\"$")
	public void the_title_should_contain_yaml(String key) throws Throwable {
		String title = util.targetEnvYaml().getString(key);
		assertTrue(util.driver().getTitle().contains(title));
	}

	// Click
	// ========================================================

	@When("^I click on id \"([^\"]*)\"$")
	public void I_click_on_id(String id) throws Throwable {
		util.clickBy(BySelector.ID, id);
	}

	@When("^I click on xpath \"([^\"]*)\"$")
	public void I_click_on_xpath(String xpath) throws Throwable {
		util.clickBy(BySelector.XPATH, xpath);
	}

	@When("^I click on \"([^\"]*)\" => \"([^\"]*)\"$")
	public void I_click_by(String by, String expr) throws Throwable {
		util.clickBy(by, expr);
	}

	// Write
	// ========================================================

	@When("^I write \"([^\"]*)\" on id \"([^\"]*)\"$")
	public void I_write_on_id(String id, String value) throws Throwable {
		util.setTextBy(BySelector.ID, id, value);
	}

	@When("^I write \"([^\"]*)\" on xpath \"([^\"]*)\"$")
	public void I_write_on_xpath(String xpath, String value) throws Throwable {
		util.setTextBy(BySelector.XPATH, xpath, value);
	}

	@When("^I write \"([^\"]*)\" on \"([^\"]*)\" => \"([^\"]*)\"$")
	public void I_write_by(String by, String expr, String value) throws Throwable {
		util.setTextBy(by, expr, value);
	}

	// Wait
	// ========================================================

	@And("^I wait (\\d+) s$")
	public void I_wait_seconds(long seconds) throws Throwable {
		util.waitMillis(seconds * 1000);
	}

	@And("^I wait (\\d+) ms$")
	public void I_wait_millis(long millis) throws Throwable {
		util.waitMillis(millis);
	}

	// Debug
	// ========================================================

	/**
	 * Fa partire la console e rimene in ascolto sullo STDIN. Permette di provare le coppie {@link BySelector} +
	 * expression.
	 */
	@And("^debug$")
	public void debug() {
		util.debug();
	}

	/**
	 * Tentivo di emulare la console interattiva pry di Ruby.
	 * <br/>
	 * Vedi <a href="https://github.com/pry/pry">https://github.com/pry/pry</a>
	 * 
	 * @throws Throwable
	 */
	@And("^pry$")
	public void pry() {
		throw new UnsupportedOperationException("Not implemented");
	}

	/*
	 * Per aggiungere la console Java è necessario aggiungere una libreria per la compilazione di sorgente a runtime
	 */
	@And("^console$")
	public void console() {
		throw new UnsupportedOperationException("Not implemented");
	}

}
