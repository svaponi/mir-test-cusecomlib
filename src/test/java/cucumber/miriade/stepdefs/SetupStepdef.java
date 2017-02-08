package cucumber.miriade.stepdefs;

import org.springframework.test.context.ContextConfiguration;

import cucumber.api.java.en.And;
import it.miriade.test.cusecomlib.CuseSpringConfiguration;
import it.miriade.test.cusecomlib.cucumber.stepdefs.Stepdef;

/**
 * <strong>ATTENZIONE</strong>
 * deve esserci almeno una classe {@link Stepdef} con `@ContextConfiguration()` e che abbia almeno uno step definito in
 * modo che il contesto Spring venga tirato su correttamente.
 * 
 * @author svaponi
 */
@ContextConfiguration(classes = CuseSpringConfiguration.class)
public class SetupStepdef implements Stepdef {

	@And("^something happens$")
	public void and_something_happens() {
		// qualsiasi cosa..
	}

}
