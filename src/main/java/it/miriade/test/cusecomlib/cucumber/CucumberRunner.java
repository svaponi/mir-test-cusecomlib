package it.miriade.test.cusecomlib.cucumber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import it.miriade.test.cusecomlib.CuseSetupConfiguration;

/**
 * Classe wrapper per invocare Cucumber con parametri custom (non hardcoded in @CucumberOptions). Per informazione sui
 * parametri vedi README.md.
 * 
 * @author svaponi
 */
@Component
@Scope("prototype")
public class CucumberRunner {

	public static final byte success = 0x0;
	public static final byte error = 0x1;
	private static final Logger log = LoggerFactory.getLogger(CucumberRunner.class);
	private static final String[] defaultPlugins = { "--plugin", CucumberSpec.PLUGIN_PRETTY, "--plugin", CucumberSpec.PLUGIN_HTML, "--plugin", CucumberSpec.PLUGIN_JSON };

	private CuseSetupConfiguration config;
	private byte exitstatus;

	@Autowired
	public CucumberRunner(CuseSetupConfiguration config) {
		super();
		this.config = config;
	}

	/**
	 * Ritorna lo condizione dell'ultima invocazione di {@link #run()}
	 * 
	 * @return
	 */
	public boolean hasErrors() {
		return exitstatus != success;
	}

	/**
	 * Lancia Cucumber con parametri letti dell'ambiente, file di properties e System Properties
	 * 
	 * @return
	 */
	public byte run() {
		return run(config);
	}

	/**
	 * Lancia Cucumber con parametri contenuti in {@link CuseSetupConfiguration}.
	 * 
	 * @param config
	 *            oggetto che incapsula la configurazione dell'ambiente
	 * @return
	 */
	public byte run(CuseSetupConfiguration config) {

		// args saranno gli argomenti che passo a Cucumber come se l'invocazione avvenisse da riga di comando
		List<String> args = buildArgs(config);

		exitstatus = 0x0;
		log.info("Running with arguments: {}", StringUtils.arrayToDelimitedString(args.toArray(), " "));
		try {
			// invoco Cucumber
			exitstatus = cucumber.api.cli.Main.run(args.toArray(new String[] {}), Thread.currentThread().getContextClassLoader());
			log.info("Cucumber terminated with status: {}", exitstatus);
		} catch (Exception e) {
			exitstatus = 0x1;
			log.info("Cucumber terminated with exception: {}", e.getMessage());
		}

		return exitstatus;
	}

	/**
	 * Costruisce gli argomenti da pasare a Cucumber. Vedi
	 * <a href="https://cucumber.io/docs/reference/jvm">documentazione</a> online.
	 * 
	 * @param config
	 * @return
	 */
	public static List<String> buildArgs(CuseSetupConfiguration config) {

		// args saranno gli argomenti che passo a Cucumber come se l'invocazione avvenisse da riga di comando
		List<String> args = new ArrayList<String>();

		// Prima metto il tag @ignore ..
		args.add("--tags");
		args.add(CucumberSpec.TAG_IGNORE);

		// .. poi aggiungo i tag che ho in input ..
		parseTags(config).forEach(tagExpr -> {

			/*
			 * Una tagExpr è una espressione di uno o più tag in OR separati da virgola. Ogni tagExpr è data in input
			 * associata all'identificatore `--tags`
			 */
			args.add("--tags");
			args.add(tagExpr);
		});

		// .. poi attacco la configurazione dei plugin di output ..
		args.addAll(Arrays.asList(defaultPlugins));

		// .. poi imposto dove cercare il "glue code" ..
		args.add("--glue");
		args.add(config.cucumberGlueClasspth());

		// .. alla fine va il path dove cercare i features (ultimo parametro senza nome)
		args.add(config.cucumberFeaturesPath());

		return args;
	}

	/*
	 * Gestione dei tags
	 */

	public static final String AND = "\\ "; // spazio, char ' '
	public static final String OR = ","; // virgola, char ','
	public static final Pattern AT = Pattern.compile("^[~]?@(.*)$"); // intervetta i tag che cominciano con la @

	/**
	 * @see #parseTags(String)
	 * @param config
	 *            {@link CuseSetupConfiguration}
	 * @return lista di tag preformattati
	 */
	public static List<String> parseTags(CuseSetupConfiguration config) {
		return parseTags(config.targetTags());
	}

	/**
	 * Interpreta l'espressione dei tag e spezza e formatta le espressioni in modo da poter costruire l'input da passare
	 * a Cucumber.<br/>
	 * <strong>Esempio</strong>. Se in input abbiamo <code>"wip fun01,fun03 ~fun02"</code>, risulta:
	 * <ul>
	 * <li>@wip</li>
	 * <li>@fun01,@fun03</li>
	 * <li>~@fun02</li>
	 * </ul>
	 * Che poi diventerà <code>"--tags @wip --tags @fun01,@fun03 --tags ~@fun02"</code> che significa: "esegui tutti i
	 * test wip (work-in-progress) delle fun01 e fun03 ma non fun02".
	 * 
	 * @param tags
	 *            unica stringa con i tag da parsare
	 * @return lista di tag preformattati
	 */
	public static List<String> parseTags(String tags) {

		List<String> args = new ArrayList<String>();
		if (StringUtils.hasText(tags)) {
			for (String arg : tags.trim().split(AND)) { // AND con char ' '

				String tmp = "";
				for (String a : arg.split(OR)) { // OR con char ','
					if (!tmp.isEmpty())
						tmp += ",";
					if (AT.matcher(a).matches())
						tmp += a;
					else // se non c'è la chiocciola la aggiungo (al posto giusto)
						tmp += a.charAt(0) == '~' ? "~@" + a.substring(1) : "@" + a;
				}
				args.add(tmp);
			}
		}

		return args;
	}
}
