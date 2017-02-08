package it.miriade.test.cusecomlib.hooks;

import it.miriade.test.cusecomlib.CuseDefaultSpec;
import it.miriade.test.cusecomlib.yaml.YamlSupport;

/**
 * Classe che estende {@link HtmlHook}. Serve SOLAMENTE per portarsi dietro il nome dello hook e stamparlo nel
 * {@link #toString()}. Tutti gli utilizzatori continueranno a vedere solo {@link HtmlHook}.
 * 
 * @see HtmlHook
 * @author svaponi
 */
public class HtmlNamedHook extends HtmlHook {

	private String name;

	public HtmlNamedHook(YamlSupport yaml, String hookName) {
		super(yaml.getMap(CuseDefaultSpec.YAML_SUPPORT_HOOKS_PREFIX + '.' + hookName));
		name = hookName;
	}

	/**
	 * JSON-like syntax
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": {\n\t name: \"" + name + "\",\n\t " + BY + ": \"" + by + "\",\n\t " + EXPR + ": \"" + expr + "\" \n}";
	}
}