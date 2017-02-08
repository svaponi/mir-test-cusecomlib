package it.miriade.test.cusecomlib.enums;

import org.springframework.util.Assert;

public enum BySelector {
	XPATH, CSS, ID, CLASS, NAME, TAG_NAME, TEXT, PARTIAL_TEXT;

	public static BySelector get(String a) {
		Assert.notNull(a, "Invalid value for BySelector");
		String by = a.startsWith(":") ? a.substring(1) : a;
		return valueOf(by.toUpperCase());
	}

	public static BySelector by(String a) {
		return get(a);
	}
}