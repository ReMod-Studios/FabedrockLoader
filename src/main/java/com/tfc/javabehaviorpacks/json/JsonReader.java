package com.tfc.javabehaviorpacks.json;

public class JsonReader {
	//TODO: move off deps for json
	public static JsonObject<JsonObject<?>> read(String input) {
		JsonObject<JsonObject<?>> object = new JsonObject<>();
		JsonObject<?> objectWorking = object;
		input = input.replace("\n","");
		input = input.replace("{","\n{");
//		System.out.println(input);
		
		for (String s : input.split("\n")) {
			boolean parsing = false;
			String text = "";

			try {
				s = s.trim().substring(1);
			} catch (Throwable ignored) {
			}
			for (char c : s.toCharArray()) {
				if (!parsing) {
					if (c == '\"' || c == '{' || c == '[' || c == ':') {
						if (c != ':') {
							text += c;
						}
						parsing = true;
					}
					if (c == '}') {
						if (objectWorking != null) {
							objectWorking = objectWorking.parent;
						}
					}
				} else {
					if (c == '\"' || c == '}' || c == ']' || c == ',') {
						if (c == '\"') {
							text += c;
						}
						parsing = false;
						if (objectWorking != null) {
							text = text.trim().replace("\n","");
							objectWorking = objectWorking.queueOrCreate(text);
						} else {
//							System.out.println(s);
//							System.out.println(text);
//							System.out.println(c);
						}
						text = "";
					} else {
						text += c;
					}
				}
			}
		}
		return object;
	}
}
