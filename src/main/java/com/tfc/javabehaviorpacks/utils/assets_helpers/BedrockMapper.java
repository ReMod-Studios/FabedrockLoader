package com.tfc.javabehaviorpacks.utils.assets_helpers;

import com.tfc.javabehaviorpacks.JavaBehaviorPacks;
import com.tfc.javabehaviorpacks.utils.PropertiesReader;

import java.io.InputStream;
import java.util.HashMap;

public class BedrockMapper {
	private static final HashMap<String, String> items = new HashMap<>();
	private static final HashMap<String, String> tabs = new HashMap<>();
	private static final HashMap<String, String> sounds = new HashMap<>();
	
	static {
		try {
			InputStream streamItems = BedrockMapper.class.getClassLoader().getResourceAsStream("assets/java-behavior-packs/item_map.properties");
			InputStream streamTabs = BedrockMapper.class.getClassLoader().getResourceAsStream("assets/java-behavior-packs/tab_map.properties");
			InputStream streamSounds = BedrockMapper.class.getClassLoader().getResourceAsStream("assets/java-behavior-packs/sound_map.properties");

			assert streamItems != null;
			assert streamTabs != null;
			assert streamSounds != null;

			String mapItems = JavaBehaviorPacks.readStream(streamItems);
			String mapTabs = JavaBehaviorPacks.readStream(streamTabs);
			String mapSounds = JavaBehaviorPacks.readStream(streamSounds);

			streamItems.close();
			streamTabs.close();
			streamSounds.close();
			
			PropertiesReader readerItems = new PropertiesReader(mapItems);
			PropertiesReader readerTabs = new PropertiesReader(mapTabs);
			PropertiesReader readerSounds = new PropertiesReader(mapSounds);
			
			for (String s : readerItems.getEntries()) items.put(s, readerItems.getValue(s));
			for (String s : readerTabs.getEntries()) tabs.put(s, readerTabs.getValue(s));
			for (String s : readerSounds.getEntries()) sounds.put(s, readerSounds.getValue(s));
		} catch (Throwable err) {
			throw new RuntimeException(err);
		}
	}
	
	public static String getMappedForItem(String bedrockName) {
		return items.getOrDefault(bedrockName,bedrockName);
	}
	
	public static String getMappedForTab(String bedrockName) {
		return tabs.getOrDefault(bedrockName,bedrockName);
	}
	
	public static String getMappedForSound(String bedrockName) {
		return sounds.getOrDefault(bedrockName,bedrockName);
	}
}
