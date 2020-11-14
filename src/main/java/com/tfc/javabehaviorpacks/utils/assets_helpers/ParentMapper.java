package com.tfc.javabehaviorpacks.utils.assets_helpers;

import com.tfc.javabehaviorpacks.JavaBehaviorPacks;
import com.tfc.javabehaviorpacks.utils.PropertiesReader;

import java.io.InputStream;
import java.util.HashMap;

public class ParentMapper {
	private static final HashMap<String, String> items = new HashMap<>();
	private static final HashMap<String, String> tabs = new HashMap<>();
	
	static {
		try {
			InputStream streamItems = ParentMapper.class.getClassLoader().getResourceAsStream("assets/java-behavior-packs/item_map.properties");
			InputStream streamTabs = ParentMapper.class.getClassLoader().getResourceAsStream("assets/java-behavior-packs/tab_map.properties");
			assert streamItems != null;
			assert streamTabs != null;
			String mapItems = JavaBehaviorPacks.readStream(streamItems);
			String mapTabs = JavaBehaviorPacks.readStream(streamTabs);
			streamItems.close();
			streamTabs.close();
			
			PropertiesReader readerItems = new PropertiesReader(mapItems);
			PropertiesReader readerTabs = new PropertiesReader(mapTabs);
			
			for (String s : readerItems.getEntries()) items.put(s, readerItems.getValue(s));
			for (String s : readerTabs.getEntries()) tabs.put(s, readerTabs.getValue(s));
		} catch (Throwable err) {
			throw new RuntimeException(err);
		}
	}
	
	public static String getMappedForItem(String bedrockName) {
		if (items.containsKey(bedrockName)) return items.get(bedrockName);
		return bedrockName;
	}
	
	public static String getMappedForTab(String bedrockName) {
		if (tabs.containsKey(bedrockName)) return tabs.get(bedrockName);
		return bedrockName;
	}
}
