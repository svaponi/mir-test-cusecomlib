# mir-test-cusecomlib

Progetto base per i test automatici sulle interfacce. Fornisce una suite di utilities per l'utilizzo di Selenium + Cucumber.

## Indice

* [How to](#come-lanciare-i-test)
  * [Come lanciare i test](#come-lanciare-i-test)
  * [Test in remoto](#test-in-remoto)
* [Note per lo sviluppatore di test](#note-sviluppatore-test)
  * [HTML Hooks](#html-hooks)
  * [Properties](#properties)
  * [Come scrivere i test](#come-scrivere-i-test)
  * [Come utilizzare i componenti della Commonlib](#come-utilizzare-commonlib)
  * [Come lanciare Cucumber da Java](#come-lanciare-cucumber-da-java)
  * [Come lanciare Cucumber da Java II](#come-lanciare-cucumber-da-java-ii)
* [Note per lo sviluppatore della Commonlib](#note-sviluppatore-cusecomlib)

## How to

### <a id="test-in-remoto"></a>Come lanciare i test

Per lanciare i test possiamo usare Maven, con questo comando

```bash
mvn test
```
Questo comando lancia tutti i test con la configurazione di default. Per impostare una configurazione a piacere usiamo le seguenti *System Property* (default in **grassetto**).

```bash
mvn test -D<chiave>=valore -D<chiave2>=valore2 ...
```

| Chiave | Valore | Descrizione |
| --- | --- | --- |
| `target.browser` | **chrome** &#124; firefox &#124; ie &#124; edge | Browser su cui eseguire i test. |
| `target.env` | **test** &#124; collaudo | Ambiente nel quale eseguire i test. |
| `target.tags` | miotag &#124; tuotag &#124; fun01 &#124; fun02 &#124; fun03 | Tags per selezionare i test da eseguire. |
| `webdriver.chrome.driver` | /path/to/driver | Localizza il webdriver di Chrome da utilizzare. |
| `webdriver.gecko.driver` | /path/to/driver | Localizza il webdriver di Firefox. |
| `webdriver.ie.driver` | /path/to/driver | Localizza il webdriver di Internet Explorer. |
| `webdriver.edge.driver` | /path/to/driver | Localizza il webdriver di Edge. |
| `webdriver.timeouts.find` | **1** &#124; N &ge; 0 | Da [documentazione](https://seleniumhq.github.io/selenium/docs/api/java/org/openqa/selenium/WebDriver.Timeouts.html): *"Specifies the amount of time the driver should wait when searching for an element if it is not immediately present.*" |
| `webdriver.timeouts.load` | **60** &#124; N &ge; 0  | Da [documentazione](https://seleniumhq.github.io/selenium/docs/api/java/org/openqa/selenium/WebDriver.Timeouts.html): *"Sets the amount of time to wait for a page load to complete before throwing an error.*" |
| `webdriver.timeouts.script` | **60** &#124; N &ge; 0  | Da [documentazione](https://seleniumhq.github.io/selenium/docs/api/java/org/openqa/selenium/WebDriver.Timeouts.html): *"Sets the amount of time to wait for an asynchronous script to finish execution before throwing an error.*" |
| `webdriver.window.position` | TOP_LEFT_X,TOP_LEFT_Y,WIDTH,HEIGHT &#124; **maximize** | Imposta la dimensione della finestra del browser. In ordine separate da virgola abbiamo le coordinate x e y dell'angolo in alto a sinistra, la langhezza e l'altezza, oppure `maximize` per massimizzare la finestra. |
| `close.browser` | **true** &#124; false | Se *FALSE* inibisce la chiusura del browser alla fine dei test. |
| `yaml.classpath.dir` | **cucumber/miriade/yaml/** | Directory base dove mettere i file YAML. **ATTENZIONE**: deve essere nel classpath, dunque dentro una delle cartelle delle risorse `src/main/resources` o `src/test/resources` |
| `screenshots.dir` | **screeenshots/** | Directory dove verranno salvati gli screenshots. |
| `on.fail` | **do-nothing** &#124; freeze &#124; debug  | **SOLO PER DEVELOPER**. Permette di bloccare l'esecuzione dei test in caso di errore/eccezione: <ul><li>se *do-nothing* si comporta normalmente: l'esecuzione termina con errore;</li> <li>se *freeze* la pagina web si blocca nella situazione che ha generato l'errore (permette allo sviluppatore di ispezionare la pagina);</li> <li>se *debug* la pagina web si blocca nella situazione che ha generato l'errore e tramite lo STDIN della console è possibile invocare i metodi existsBy, clickBy, findBy e setTextBy del `CuseUtil` (es. per testare a mano le espressioni degli hook).</li></ul> |
| `use.properties` | path/to/file.properties | Localizza un file di properties con i valori delle properties da utilizare per la esecuzione corrente. |
| `cucumber.glue` | **cucumber/miriade** | Classpath in cui Cucumber cerca gli stepdef. |
| `cucumber.properties` | **src/test/resources/cucumber/miriade** | Path in cui Cucumber cerca i *.features*, cioè i file di test scritti in linguaggio [Gherkin](https://github.com/cucumber/cucumber/wiki/Gherkin). |


> I valori di `target.tags` specificati sono un esempio. Nella implementazione reale dipenderanno dai casi di test implementati e dai tag a piacere ad essi associati (per info sui tag vedi [documentazione](https://github.com/cucumber/cucumber/wiki/Tags) online).
> E' possibile specificare una combinazione di tag, esempio `"~inprogress online,login"` (il tutto tra apici), il che significa: esegui tutti gli scenari taggati NOT **@inprogress** AND (**@online** OR **@login**). La '~' funge da negazione. Gli AND hanno precedenza sugli OR.
> E' possibile utilizzare il tag **@ignore** nei .feature file per escludere features e/o scenari (questo tag è già presente nella configurazione di default della Commonlib, non serve aggiungerlo ai target.tags).

> Il valore di `webdriver.?.driver` può essere un path assoluto della macchina locale (sulla quale girano i test) oppure un URL web al quale risponde il webdriver remoto, è necessario specificare anche il protocollo (http o https). ***Questo valore stabilisce implicitamente se l'esecuzione avviene in locale oppure in remoto***.
> Se il valore di `webdriver.?.driver` non viene settato, è possibile recuperare il path al driver locale dal SO (invocando un `which <nome-driver>`, solo in ambiente Unix). Per abilitare questa funzione aggiungere `-Dinspect.os` al comando di invocazione dei test.


### <a id="test-in-remoto"></a>Test in remoto

Quando eseguiamo i test su una macchina in remoto, il browser e i log del driver appaiono ovviamente su quella macchina. Per vedere quello che succede dobbiamo accedere alla macchina in RDP o in `ssh -x`.

- - - -

## <a id="note-sviluppatore-test"></a>Note per lo sviluppatore di test

### <a id="html-hooks"></a>HTML Hooks

**Questi hooks no hanno nulla a che vedere con i *@Before-Hook* e *@After-Hook* di Cucumber**.

Un [HtmlHook](?it/miriade/test/cusecomlib/hooks/HtmlHook.html) è un componente della Commonlib che implementa il "collegamento" ad un elemento HTML all'interno del DOM della pagina web. Serve per facilitare la scrittura dei test.
Un hook è composto da due elementi:
 * `by` ([BySelector](?it/miriade/test/cusecomlib/enums/BySelector.html)): è la tipologia del selettore, ovvero `xpath`, `id`, `class`, `css`, `tag_name`, `text`.
 * `expr` (expression): è una espressione da interpretare secondo la sua tipologia che identifica l'elemento HTML.

Esempio:
> { by: "css", expr: "input#username" } identifica l'elemento `<input id="username" />`

### <a id="properties"></a>Properties

Per passare parametri/proprietà all'ambiente abbiamo più possibilità, ovvero in ordine di priorità crescente (le ultime sovrascrivono le prime):
* impostarle in un file `cuse.properties` da mettere nel classpath del progetto di test che utilizza la Commonlib
* settarle come *System Property* al momento dell'invocazione dei test (come visto in [Come lanciare i test](#come-lanciare-i-test))
* impostarle in un file `.properties` a piacere che verrà passato con la *System Property* `use.properties` al momento dell'invocazione dei test, es. `mvn test ... -Duse.properties=./my.properties`


### <a id="come-scrivere-i-test"></a>Come scrivere i test

Per scrivere un test dobbiamo creare i seguenti "pezzi".

* ***.feature*** file. Questi file contengono la logica del test e sono scritti in un linguaggio naturale detto Gherkin (vedi [documentazione](https://github.com/cucumber/cucumber/wiki/Gherkin) online). Questi file vanno salvati in `src/test/resources/cucumber/miriade`. Esempio:

  ```
  # language: it

  @online
  Funzionalità: Webmail è online

  	Scenario: vado su Webmail e controllo il titolo della pagina
  		Dato che sono su Webmail
  		Allora verifico che il titolo sia "Webmail"
  ```
  > Ogni singola frase che scrivo nel .feature file è uno **step** (eccetto le *Funzionalità* e gli *Scenario* che sono i "titoli" dei miei test). Tutti gli step che scrivo dovranno poi essere definiti/implementati negli *Stepdef*.

* ***Stepdef*** o definizione degli step. Le definizioni degli step sono scritte in Java e vanno messe in una o più classi che implementano `it.miriade.test.cucumber.stepdefs.Stepdef`. Queste classi vanno messe in un qualsiasi sottopackage di `cucumber.miriade`, generalmente useremo `cucumber.miriade.stepdefs` o per maggiore chiarezza `cucumber.miriade.nomeprogetto.stepdefs`. Esempio:

  ```java
  package cucumber.miriade.mweb.stepdefs;

  import ... // qualsiasi IDE vi aggiungerà in automatico gli import mentre scrivete il codice

  /**
   * Verifica che l'applicazione sia raggiungibile in rete.
   */
  public class OnlineStepdef implements Stepdef {

  	@Autowired
  	private Online webmail;

  	@Autowired
  	private CuseUtil util;

  	@Dato("^che sono su Webmail$")
  	public void dato_che_sono_su_Webmail() {
  		webmail.go();
  	}

  	@Allora("^verifico che il titolo sia \"([^\"]*)\"$")
  	public void allora_verifico_che_il_titolo_sia(String title) {
  		if (!webmail.isTitleEquals(title))
  			throw new RuntimeException("Il titolo non è corretto");
  	}
  }
  ```

* ***PageObject***. Sono gli oggetti che contengono la vera logica, dove c'è la maggior parte del codice (per info sui *PageObject* vedi [questo articolo](http://artoftesting.com/automationTesting/pageObjectModel.html), oppure sfoglia [questi risultati](https://www.google.it/search?q=Page+Object+architecture)). Anche i *PageObject* vanno creati in un qualsiasi sottopackage di `cucumber.miriade`, generalmente useremo `cucumber.miriade.pageobjs` o per maggiore chiarezza `cucumber.miriade.${nome.progetto}.pageobjs`. Seguendo il nostro esempio avremo:

  ```java
  package cucumber.miriade.mweb.pageobjs;

  import ...

  /**
   * Pagina principale di Webmail
   */
  @Component
  public class Online {

  	@Autowired
  	private CuseUtil util;

  	/**
  	 * Vado sulla home di Webmail
  	 */
  	public void go() {
  		String url = util.targetEnvYaml().getString("url");
  		String cookieName = util.commonYaml().getString("cookies.presentation_video_shown");
  		boolean videoShown;
  		do { // ricarico la pagina finchè non si ripresenta più il video di presentazione
  			videoShown = util.hasCookie(cookieName);
  			util.loadPage(url);
  			util.waitMillis(500);
  		} while (!videoShown);
  	}

  	/**
  	 * Verifico se il titolo è come l'input
  	 *
  	 * @param title
  	 * @return
  	 */
  	public boolean isTitleEquals(String title) {
  		return util.currentTitle().equals(title);
  	}
  }

  ```
  > **NOTA BENE**: l'oggetto `util` è un componente della Commonlib e utilizzabile in tutte le classi Java contenute nel opackage `cucumber.miriade` o sottopackages (vedi [CuseUtil API](?it/miriade/test/cusecomlib/CuseUtil.html) online). Questo è possibile grazie alla annotation `@Autowired` di Spring, per info vedi [questi risultati sulla Dependecy Injection](https://www.google.it/search?q=dependency+injection+spring).


I test così definiti saranno eseguiti da Cucumber. Per lanciare Cucumber posso usare entrambi gli approcci:
* [Come lanciare Cucumber da Java](#come-lanciare-cucumber-da-java)
* [Come lanciare Cucumber da Java II](#come-lanciare-cucumber-da-java-ii)


### <a id="come-utilizzare-commonlib"></a>Come utilizzare i componenti delle Commonlib

Per informazioni vedi la [documentazione](https://github.com/svaponi/mir-test-cusecomlib/wiki) online.


### <a id="come-lanciare-cucumber-da-java"></a>Come lanciare Cucumber da Java

Un possibile approccio è usare la configurazione suggerita dalla [documentazione](https://cucumber.io/docs/reference/jvm#java) online di Cucumber, ovvero impostare una classe runner di JUnit annotata `@RunWith(Cucumber.class)`, con la configurazione di Cucumber tramite `@CucumberOptions` annotation. Esempio

```java
@RunWith(Cucumber.class)
@CucumberOptions(
	tags = { CucumberSpec.TAG_IGNORE },
	plugin = { CucumberSpec.PLUGIN_PRETTY, CucumberSpec.PLUGIN_HTML, CucumberSpec.PLUGIN_JSON },
	features = { CucumberSpec.FEATURES },
	glue = { CucumberSpec.GLUE }
)
public class RunWithCucumberTest {

}
```
I parametri hardcoded nella annotation `@CucumberOptions` si possono sovrascrivere con la *System Property* `cucumber.options`, settabile da rida di comando (per informazioni sulle opzioni vedi [documentazione](https://cucumber.io/docs/reference/jvm#configuration) online). Esempio

```bash
mvn test -Dcucumber.options="--tags @miriade --glue new/path/to/stepdefs --features new/path/to/features"
```

### <a id="come-lanciare-cucumber-da-java-ii"></a>Come lanciare Cucumber da Java II

Un altro aproccio è utilizzare il runner JUnit di Spring (*SpringJUnit4ClassRunner*, necessario per inizializzare il contesto) e poi invocare il *CucumberRunner*. In questo modo abbiamo la possibilità di modificare dinamicamente - da codice - la configurazione dei test.

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CuseSpringConfiguration.class)
public class CucumberTest {

	@Autowired
	private CucumberRunner cucumber;

	@Autowired
	private CuseSetupConfiguration config;

	@Test
	public void test() throws Throwable {

		config.targetBrowser(Browser.FIREFOX);
		config.windowPos("0,0,640,480");
		// fai altre cose con config....

		byte status = cucumber.run(config);
		if (status != CucumberRunner.success)
			throw new RuntimeException("Cucumber terminated with errors");
	}
}
```

> **IMPORTANTE**: ricordarsi di lanciare una eccezione se lo status di ritorno è diverso da zero. Oppure, se stiamo lanciando il runner all'interno di un metodo main(), invocare System.exit(status).


## <a id="note-sviluppatore-cusecomlib"></a>Note per lo sviluppatore della Commonlib

> **SOLO PER DEVELOPER**. Durante gli sviluppi della Commonlib è plausibile voler invocare dei test di integrazione che utilizzino un webdriver locale o remoto per verificare le funzionalità della libreria. Questi test sono normalmente disabilitati. Per abilitare i test di integrazione sulla Commonlib aggiungere `-Drun.integration.tests` al comando di invocazione degli unit test sulla libreria.
