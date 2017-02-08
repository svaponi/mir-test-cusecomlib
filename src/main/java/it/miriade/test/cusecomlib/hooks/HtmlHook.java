package it.miriade.test.cusecomlib.hooks;

import java.util.Map;

import it.miriade.test.cusecomlib.CuseUtil;
import it.miriade.test.cusecomlib.enums.BySelector;

/**
 * <strong>Questi hook no hanno nulla c he vedere con i <i>Before Hook</i> e
 * <a href="https://cucumber.io/docs/reference#after"><i>After Hook</i></a> di
 * Cucumber.</strong><br/>
 * Classe per incapsulare il riferimento ad un elemento della pagina HTML. Viene usato dal {@link CuseUtil} per
 * accedere a tale elemento.
 * 
 * @author svaponi
 */
public class HtmlHook {

	/**
	 * key della tipologia di selettore
	 */
	public static final String BY = "by";

	/**
	 * key della espressione del selettore
	 */
	public static final String EXPR = "expr";

	/**
	 * Tipologia dell'espressione, ovvero come interpretare {@link #expr}. Tutte le tipologie sono incapsulate in
	 * {@link BySelector}.
	 */
	final public BySelector by;

	/**
	 * Espressione da interpretare
	 */
	final public String expr;

	/**
	 * Costruisce un {@link HtmlHook} con la tipologia e l'espressione in input.
	 * 
	 * @param by
	 *            tipologia
	 * @param expr
	 *            espressione
	 */
	public HtmlHook(BySelector by, String expr) {
		super();
		this.by = by;
		this.expr = expr;
	}

	/**
	 * Costruisce un {@link HtmlHook} con la tipologia e l'espressione in input.
	 * 
	 * @param by
	 *            stringa che identifica una valore di {@link BySelector}
	 * @param expr
	 *            espressione
	 */
	public HtmlHook(String by, String expr) {
		this(BySelector.get(by), expr);
	}

	/**
	 * Costruisce un {@link HtmlHook} con i dati in mappa. La mappa deve contenere le chiavi {@value #BY} e {@value #EXPR}
	 * con rispettivamente la tipologia e l'espressione che costituiscono l'hook.
	 * 
	 * @param map
	 */
	public HtmlHook(Map<String, ?> map) {
		super();
		if (map == null || !map.containsKey(EXPR) || !map.containsKey(BY))
			throw new IllegalArgumentException("Invalid hook Map");
		this.by = BySelector.get((String) map.get(BY));
		this.expr = (String) map.get(EXPR);
	}

	/**
	 * JSON-like syntax
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": {\n\t " + BY + ": \"" + by + "\",\n\t " + EXPR + ": \"" + expr + "\" \n}";
	}

}