package com.tfc.javabehaviorpacks.utils.assets_helpers;

import net.minecraft.util.Identifier;

public class NoValidationIdentifier extends Identifier {
	private final String namespace;
	private final String path;
	
	public NoValidationIdentifier(String namespace, String path) {
		super("a");
		this.namespace = namespace;
		this.path = path;
	}
	
	public NoValidationIdentifier(String id) {
		super("a");
		String[] strings = id.split(":", 2);
		if (strings.length == 1) {
			namespace = "minecraft";
			path = strings[0];
		} else {
			namespace = strings[0];
			path = strings[1];
		}
	}
	
	@Override
	public String getNamespace() {
		return namespace;
	}
	
	@Override
	public String getPath() {
		return path;
	}
	
	@Override
	public String toString() {
		return namespace + ":" + path;
	}
	
	@Override
	public boolean equals(Object object) {
		return
				object instanceof Identifier &&
						((Identifier) object).getNamespace().equals(this.getNamespace()) &&
						((Identifier) object).getNamespace().equals(this.getPath());
	}
}
