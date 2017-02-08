package it.miriade.test.cusecomlib;

import java.io.File;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import it.miriade.test.cusecomlib.CuseUtil;
import it.miriade.test.cusecomlib.cucumber.stepdefs.Stepdef;

/**
 * Stepdef usato per testare la funzionalità {@link CuseUtil#takeScreenshot()}
 * 
 * @author svaponi
 */
public class ScreenshotStepdef implements Stepdef {

	private CuseUtil seleniumUtil;
	private File screenshot;

	public ScreenshotStepdef(CuseUtil seleniumUtil) {
		super();
		this.seleniumUtil = seleniumUtil;
	}

	@Given("^load a page$")
	public void given_load_a_page() {
		seleniumUtil.loadPage("www.google.it");

	}

	@And("^take a screenshot$")
	public void and_takes_a_screenshot() {
		screenshot = seleniumUtil.takeScreenshot();
	}

	public File getScreenshot() {
		return screenshot;
	}
}