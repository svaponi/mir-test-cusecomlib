package cucumber.miriade.stepdefs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import it.miriade.test.cusecomlib.CuseSpringConfiguration;
import it.miriade.test.cusecomlib.CuseUtil;
import it.miriade.test.cusecomlib.cucumber.stepdefs.Stepdef;

/**
 * Classe {@link Stepdef} che carica il contesto e definisce gli hooks.
 * 
 * @author svaponi
 */
@ContextConfiguration(classes = CuseSpringConfiguration.class)
public final class ContextConfigStepdef implements Stepdef {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	protected CuseUtil util;

	@After
	public void afterScenario(Scenario scenario) {
		log.info("Scenario \"{}\" {}", scenario.getName(), scenario.isFailed() ? "FAILED" : "SUCCEEDED");
		if (scenario.isFailed())
			util.toDoOnFail();
	}

}
