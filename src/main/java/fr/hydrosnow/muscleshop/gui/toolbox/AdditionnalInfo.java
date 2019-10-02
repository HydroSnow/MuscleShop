package fr.hydrosnow.muscleshop.gui.toolbox;

import java.util.HashMap;
import java.util.Map;

public class AdditionnalInfo {
	private final Map<String, Object> info;

	public AdditionnalInfo() {
		info = new HashMap<>();
	}

	public void add(final String str, final Object obj) {
		info.put(str, obj);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(final String str) {
		return (T) info.get(str);
	}
}
