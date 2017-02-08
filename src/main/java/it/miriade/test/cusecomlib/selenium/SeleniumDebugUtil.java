package it.miriade.test.cusecomlib.selenium;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.springframework.util.StringUtils;

import it.miriade.test.cusecomlib.CuseUtil;
import it.miriade.test.cusecomlib.enums.BySelector;

/**
 * <strong>SOLO PER DEVELOPER</strong><br/>
 * Utility per interagire con {@link CuseUtil} tramite lo STDIN della console.
 * 
 * @see #start(CuseUtil)
 * @author svaponi
 */
public class SeleniumDebugUtil {

	private static final String SEP = ",";

	/**
	 * Nascondo il costruttore, uso solo metodi statici
	 */
	private SeleniumDebugUtil() {
		super();
	}

	/**
	 * Quando invocato rimene in ascolto sullo STDIN della console java. Con una particolare sintassi possiamo
	 * utilizzare Selenium per provare
	 * le espressioni. La sintassi è la seguente:
	 * 
	 * <pre>
	 * action,{@link BySelector},expression
	 * </pre>
	 * 
	 * Dove action corrisponde ai metodi di {@link CuseUtil}:
	 * <ul>
	 * <li>find => {@link CuseUtil#findBy(String, String)}</li>
	 * <li>exist|exists => {@link CuseUtil#existsBy(String, String)}</li>
	 * <li>click => {@link CuseUtil#clickBy(String, String)}</li>
	 * <li>set|setText|test|write => {@link CuseUtil#setTextBy(String, String, String)} attenzione che il valore è
	 * inserito come secondo</li>
	 * <ul>
	 * <br/>
	 * Esempio;
	 * 
	 * <pre>
	 * set,YYX1234,css,#login-form .username
	 * set,*******,css,#login-form .password
	 * click,css,.login-button
	 * </pre>
	 * 
	 * Oppure, se abbiamo il supporto per i file YAML configurato (ovvero common.yml dove settiamo tutti gli hooks),
	 * possiamo usare direttamente gli hooks. Esempio:
	 * 
	 * <pre>
	 * set,login_username,YYX1234
	 * set,login_password,*******
	 * click,login_click
	 * </pre>
	 */
	public static void start(CuseUtil seleniumUtil) {
		System.out.println();
		try (Scanner in = new Scanner(System.in)) {
			String[] args;
			String line = "";
			boolean running = true;
			reading: while (running) {
				try {
					System.out.print("DEBUG > ");
					line = in.nextLine();
					if ("exit".equalsIgnoreCase(line)) {
						System.err.println();
						running = false;
						break reading;
					}

					args = line.split(SEP);
					System.out.println("Args: " + Arrays.deepToString(args));

					if (args.length < 2) {
						System.err.println("Istruzione errata");
						continue reading;
					}

					for (int i = 0; i < args.length; i++)
						if (StringUtils.hasText(args[i]))
							args[i] = args[i].trim();

					String command = args[0];
					List<WebElement> elems;
					boolean result;
					switch (command) {
					case "find":
						if (args.length > 2) {
							// args[1] è il tipo di selettore
							// args[2] è la espressione
							elems = seleniumUtil.findBy(args[1], args[2]);
						} else {
							// args[1] è il nome di un hook
							elems = seleniumUtil.findBy(args[1]);
						}
						System.out.printf("Found %d element%s\n", elems.size(), elems.size() == 1 ? "" : "s");
						elems.forEach(el -> System.out.println(el));
						break;
					case "exist":
					case "exists":
						if (args.length > 2) {
							// args[1] è il tipo di selettore
							// args[2] è la espressione
							result = seleniumUtil.existsBy(args[1], args[2]);
						} else {
							// args[1] è il nome di un hook
							result = seleniumUtil.existsBy(args[1]);
						}
						System.out.printf("Element %s\n", result ? "EXISTS" : "NOT EXIST");
						break;
					case "click":
						if (args.length > 2) {
							// args[1] è il tipo di selettore
							// args[2] è la espressione
							seleniumUtil.clickBy(args[1], args[2]);
						} else {
							// args[1] è il nome di un hook
							seleniumUtil.clickBy(args[1]);
						}
						System.out.printf("Clicked \n");
						break;
					case "set":
					case "setText":
					case "text":
					case "write":
						if (args.length > 3) {
							// args[1] è il tipo di selettore
							// args[2] è la espressione
							// args[3] è il valore da settare
							seleniumUtil.setTextBy(args[2], args[3], args[1]);
						} else {
							// args[1] è il nome di un hook
							// args[2] è il valore da settare
							seleniumUtil.setTextBy(args[2], args[1]);
						}
						System.out.printf("Set text: %s\n", args[1]);
						break;
					case "send":
					case "sendKey":
					case "sendKeys":
					case "key":
					case "keys":
						Keys keysToSend = Keys.valueOf(args[1]);
						seleniumUtil.driver().switchTo().activeElement().sendKeys(keysToSend);
						System.out.printf("Sent key: %s\n", keysToSend);
						break;
					default:
						System.err.println("Comando inesistente");
						break;
					}

				} catch (Exception e) {
					System.err.println(e.getMessage());

				} // fine try dentro il while
			} // fine while
		} // fine try-with-resources Scanner
	}

}
