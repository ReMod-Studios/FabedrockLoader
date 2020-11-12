package com.tfc.javabehaviorpacks.json;

import java.util.HashMap;

public class JsonObject<T> {
	private final HashMap<String,JsonObject<?>> objectHashMap = new HashMap<>();
	private T held = null;
	public JsonObject parent;
	
	private String queuedName;
	
	public JsonObject() {
	}
	
	public JsonObject(T object) {
		held = object;
	}
	
	public JsonObject(T object, JsonObject<?> parent) {
		held = object;
		this.parent = parent;
	}
	
	public <A> void putEntry(String entry, A value) {
		objectHashMap.put(entry,new JsonObject<>(value));
	}
	
	public T getHeld() {
		return held;
	}
	
	public JsonObject<?> get(String entry) {
		return objectHashMap.get(entry);
	}
	
	public JsonObject<?> queueOrCreate(String text) {
		try {
			if (queuedName == null) {
				queuedName = text;
			} else {
				if (text.startsWith("{")) {
					return objectHashMap.put(queuedName, JsonReader.read(text));
				} else if (text.startsWith("[")) {
					//TODO: lists
				} else {
					if (text.equals("true")) objectHashMap.put(queuedName, new JsonObject<>(true));
					else if (text.equals("false")) objectHashMap.put(queuedName, new JsonObject<>(false));
					else if (text.equals("TRUE")) objectHashMap.put(queuedName, new JsonObject<>(true));
					else if (text.equals("FALSE")) objectHashMap.put(queuedName, new JsonObject<>(false));
					else if (text.startsWith("\"")) objectHashMap.put(queuedName, new JsonObject<>(text));
					else if (text.contains("."))
						objectHashMap.put(queuedName, new JsonObject<>(Double.parseDouble(text)));
					else objectHashMap.put(queuedName, new JsonObject<>(Integer.parseInt(text)));
				}
				queuedName = null;
			}
		} catch (Throwable ignored) {
			System.out.println(queuedName);
			System.out.println(text);
		}
		return this;
	}
	
	@Override
	public String toString() {
		return "JsonObject{" +
				"objectHashMap=" + objectHashMap +
				", held=" + held +
				'}';
	}
}
