package it.miriade.test.cusecomlib.cucumber;

/**
 * Contenitore di variabili e specifiche di Cucumber.
 * 
 * @author svaponi
 */
public interface CucumberSpec {

	/**
	 * Tag preconfigurato per escludere scenario e/o features
	 */
	public String TAG_IGNORE = "~@ignore";

	/**
	 * Plugins for output format on console
	 */
	public String PLUGIN_PRETTY = "pretty";

	/**
	 * Plugins for output HTML format
	 */
	public String PLUGIN_HTML = "html:target/cucumber";

	/**
	 * Plugins for output JSON format
	 */
	public String PLUGIN_JSON = "json:target/cucumber.json";

	/**
	 * Classpath to test-steps implementation
	 */
	public String GLUE = "cucumber/miriade";

	/**
	 * Path to .features files
	 */
	public String FEATURES = "src/test/resources/cucumber/miriade";

	/*
	 * Parametri per la configurazione di Cucumber
	 */

	/**
	 * Parametri per sovrascrivere i valori di default delle opzioni di Cucumber.<br/>
	 * Vedi <a href=
	 * "https://cucumber.io/docs/reference/jvm#configuration">https://cucumber.io/docs/reference/jvm#configuration</a>
	 */
	public String CUCUMBER_OPTIONS = "cucumber.options";

	/**
	 * TODO: <strong>to be implemented!</strong><br/>
	 * <br/>
	 * Proprietà che indica quali plugin utilizzare per l'output di Cucumber. Di default vengono configurati i tre
	 * Vedi <a href="https://cucumber.io/docs/reference#reports">https://cucumber.io/docs/reference#reports</a>
	 */
	public String OVERRIDE_PLUGINS = "cucumber.plugins";

	/**
	 * TODO: <strong>to be implemented!</strong><br/>
	 * <br/>
	 * Proprietà che indica quali plugin utilizzare per l'output di Cucumber, oltre ai tre plugin preconfigurati dalla
	 * commn-lib: pretty, json e html.<br/>
	 * Vedi <a href="https://cucumber.io/docs/reference#reports">https://cucumber.io/docs/reference#reports</a>
	 */
	public String ADD_PLUGINS = "cucumber.plugins.add";

	/**
	 * Proprietà che indica il classpath in cui Cucumber cerca gli stepdef. Il default impostato dalla common-lib è
	 * {@value #GLUE}.<br/>
	 */
	public String OVERRIDE_GLUE = "cucumber.glue";

	/**
	 * Proprietà che indica il path in cui Cucumber cerca i .features, file di test scritti in linguaggio Gherkin. Il
	 * default impostato dalla common-lib è {@value #FEATURES}.<br/>
	 * Vedi <a href="https://cucumber.io/docs/reference#gherkin">https://cucumber.io/docs/reference#gherkin</a>
	 */
	public String OVERRIDE_FEATURES = "cucumber.features";
}