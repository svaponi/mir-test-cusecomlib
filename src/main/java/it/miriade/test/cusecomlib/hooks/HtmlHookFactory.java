package it.miriade.test.cusecomlib.hooks;

import it.miriade.test.cusecomlib.CuseDefaultSpec;
import it.miriade.test.cusecomlib.yaml.YamlSupport;

/**
 * Factory per creare oggetti di tipo {@link HtmlHook}.<br/>
 * Se utilizza uno YAML, tale file deve avere la struttura standard, ovvero tutti gli hook devono essere sotto il parent
 * comune {@link CuseDefaultSpec#YAML_SUPPORT_HOOKS_PREFIX} (vedi documentazione)
 * 
 * @author svaponi
 */
public class HtmlHookFactory {

	private YamlSupport _yaml;

	/**
	 * @param yaml
	 *            {@link YamlSupport} per il file che contiene gli hooks
	 */
	public HtmlHookFactory(YamlSupport yaml) {
		super();
		this._yaml = yaml;
	}

	// YamlSupport
	// ========================================================================

	private YamlSupport yaml() throws UnsupportedOperationException {
		if (_yaml == null)
			throw new UnsupportedOperationException(CuseDefaultSpec.YAML_UNSUPPORTED_MESSAGE);
		return _yaml;
	}

	// ========================================================================

	/**
	 * Estrae un oggetto {@link HtmlHook} dal common.yml.
	 * <strong>ATTENZIONE</strong>: lo YAML deve avere la struttura standard (vedi documentazione)
	 * 
	 * @param hookName
	 * @return
	 * @throws UnsupportedOperationException
	 *             se manca lo YAML
	 */
	public HtmlHook build(String hookName) throws UnsupportedOperationException {
		return new HtmlNamedHook(yaml(), hookName);
	}

	/**
	 * Estrae un oggetto {@link HtmlHook} dallo {@link YamlSupport} in input.
	 * <strong>ATTENZIONE</strong>: lo YAML deve avere la struttura standard (vedi documentazione)
	 * 
	 * @param yaml
	 * @param hookName
	 * @return
	 * @throws UnsupportedOperationException
	 *             se manca lo YAML
	 */
	public static HtmlHook build(YamlSupport yaml, String hookName) throws UnsupportedOperationException {
		return new HtmlNamedHook(yaml, hookName);
	}

}