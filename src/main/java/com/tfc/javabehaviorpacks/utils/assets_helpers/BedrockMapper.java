package com.tfc.javabehaviorpacks.utils.assets_helpers;

import com.tfc.javabehaviorpacks.JavaBehaviorPacks;
import com.tfc.javabehaviorpacks.utils.PropertiesReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class BedrockMapper {
	private static final HashMap<String, String> items = new HashMap<>();
	private static final HashMap<String, String> tabs = new HashMap<>();
	private static final HashMap<String, String> sounds = new HashMap<>();
	private static final HashMap<String, String> tags = new HashMap<>();
	private static final HashMap<String, String> particles = new HashMap<>();
	
	static {
		try {
			InputStream streamItems = BedrockMapper.class.getClassLoader().getResourceAsStream("assets/java-behavior-packs/mappings/item_map.properties");
			InputStream streamTabs = BedrockMapper.class.getClassLoader().getResourceAsStream("assets/java-behavior-packs/mappings/tab_map.properties");
			InputStream streamSounds = BedrockMapper.class.getClassLoader().getResourceAsStream("assets/java-behavior-packs/mappings/sound_map.properties");
			InputStream streamTags = BedrockMapper.class.getClassLoader().getResourceAsStream("assets/java-behavior-packs/mappings/tag_map.properties");
			InputStream streamParticles = BedrockMapper.class.getClassLoader().getResourceAsStream("assets/java-behavior-packs/mappings/particle_map.properties");

			assert streamItems != null;
			assert streamTabs != null;
			assert streamSounds != null;
			assert streamTags != null;
			assert streamParticles != null;

			String mapItems = JavaBehaviorPacks.readStream(streamItems);
			String mapTabs = JavaBehaviorPacks.readStream(streamTabs);
			String mapSounds = JavaBehaviorPacks.readStream(streamSounds);
			String mapTags = JavaBehaviorPacks.readStream(streamTags);
			String mapParticles = JavaBehaviorPacks.readStream(streamParticles);

			streamItems.close();
			streamTabs.close();
			streamSounds.close();
			streamTags.close();
			streamParticles.close();
			
			PropertiesReader readerItems = new PropertiesReader(mapItems);
			PropertiesReader readerTabs = new PropertiesReader(mapTabs);
			PropertiesReader readerSounds = new PropertiesReader(mapSounds);
			PropertiesReader readerTags = new PropertiesReader(mapTags);
			PropertiesReader readerParticles = new PropertiesReader(mapParticles);
			
			for (String s : readerItems.getEntries()) items.put(s, readerItems.getValue(s));
			for (String s : readerTabs.getEntries()) tabs.put(s, readerTabs.getValue(s));
			for (String s : readerSounds.getEntries()) sounds.put(s, readerSounds.getValue(s));
			for (String s : readerTags.getEntries()) tags.put(s, readerTags.getValue(s));
			for (String s : readerParticles.getEntries()) particles.put(s, readerParticles.getValue(s));
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
	
	public static String getMappedForParticle(String bedrockName) {
		return particles.getOrDefault(bedrockName,bedrockName);
	}
	
	public static ArrayList<String> getAllTags(String bedrockName) {
		ArrayList<String> allTags = new ArrayList<>();
		tags.forEach((java,bedrock)->{
			if (bedrock.equals(bedrockName)) {
				allTags.add(java.replace("\\",""));
			}
		});
		return allTags;
	}
}
