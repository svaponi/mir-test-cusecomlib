package it.miriade.test.cusecomlib.utils;

import org.springframework.core.env.PropertySource;

public class CustomPropertySource extends PropertySource<String> {

	public CustomPropertySource() {
		super("custom");
	}

	@Override
	public String getProperty(String name) {
		System.out.println("CustomPropertySource: " + name);
		return "";
	}
}
