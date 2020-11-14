package com.tfc.javabehaviorpacks.client;

import com.google.common.collect.ImmutableSet;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.tfc.javabehaviorpacks.JavaBehaviorPacks;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;

//TODO: optimizations, block models
public class BedrockResourcePackAdapter implements ResourcePack {
	private static InputStream icon;
	
	private ArrayList<InputStream> toClose = new ArrayList<>();
	
	@Override
	public InputStream openRoot(String fileName) throws IOException {
		icon = BedrockResourcePackAdapter.class.getClassLoader().getResourceAsStream("assets/java-behavior-packs/bedrock.png");
		return icon;
	}
	
	@Override
	public InputStream open(ResourceType type, Identifier id) throws IOException {
		if (id.toString().endsWith(".json")) {
			HashMap<Identifier, String> jsons = new HashMap<>();
			
			JavaBehaviorPacks.clientItemJsons.forEach(provider -> jsons.put(new Identifier(provider.getT().toString().toLowerCase()), provider.getV()));
			JavaBehaviorPacks.clientLangs.forEach(provider -> jsons.put(new Identifier(provider.getT().toString().toLowerCase()), provider.getV()));
			
			InputStream result = new ByteArrayInputStream(jsons.get(id).getBytes());
			toClose.add(result);
			
			return result;
		} else if (id.toString().endsWith(".png")) {
			HashMap<Identifier, File> textures = new HashMap<>();
			
			JavaBehaviorPacks.clientTextures.forEach(provider -> {
				textures.put(new Identifier(provider.getT().toString().toLowerCase()), provider.getV());
			});
			
			FileInputStream result = new FileInputStream(textures.get(id));
			toClose.add(result);
			
			return result;
		}
		
		return null;
	}
	
	@Override
	public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter) {
		System.out.println(namespace);
		System.out.println(prefix);
		
		ArrayList<Identifier> identifiers = new ArrayList<>();
		JavaBehaviorPacks.clientItemJsons.forEach(provider -> identifiers.add(new Identifier(provider.getT().toString().toLowerCase())));
		JavaBehaviorPacks.clientTextures.forEach(provider -> identifiers.add(new Identifier(provider.getT().toString().toLowerCase())));
		JavaBehaviorPacks.clientLangs.forEach(provider -> identifiers.add(new Identifier(provider.getT().toString().toLowerCase())));
		
		System.out.println(
				Arrays.deepToString(identifiers.toArray())
		);
		
		return identifiers;
//		return ImmutableSet.of();
	}
	
	@Override
	public boolean contains(ResourceType type, Identifier id) {
		ArrayList<Identifier> identifiers = new ArrayList<>();
		
		JavaBehaviorPacks.clientItemJsons.forEach(provider -> identifiers.add(new Identifier(provider.getT().toString().toLowerCase())));
		JavaBehaviorPacks.clientTextures.forEach(provider -> identifiers.add(new Identifier(provider.getT().toString().toLowerCase())));
		JavaBehaviorPacks.clientLangs.forEach(provider -> identifiers.add(new Identifier(provider.getT().toString().toLowerCase())));

//		System.out.println(
//				Arrays.deepToString(identifiers.toArray())
//		);

//		if (id.toString().endsWith(".png")) {
//			System.out.println(id);
//			System.out.println(identifiers.contains(id));
//			identifiers.forEach((identifier)->{
//				if (identifier.toString().endsWith(".png")) {
//					System.out.println(identifier);
//				}
//			});
//		}
		
		return identifiers.contains(id);
	}
	
	@Override
	public Set<String> getNamespaces(ResourceType type) {
		return ImmutableSet.copyOf(JavaBehaviorPacks.namespaces.toArray(new String[0]));
	}
	
	@Override
	public <T> @Nullable T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException {
		return metaReader.fromJson(
				//Using Gson here because it's required
				new GsonBuilder().setLenient().setPrettyPrinting().create().fromJson("" +
						"{\n" +
						"\"pack_format\": 6,\n" +
						"\"description\": \"Bedrock resource pack loader\"\n" +
						"}", JsonObject.class)
		);
	}
	
	@Override
	public String getName() {
		return "Bedrock Assets";
	}
	
	@Override
	public void close() {
		try {
			icon.close();
			toClose.forEach((in) -> {
				try {
					in.close();
				} catch (Throwable ignored) {
				}
			});
		} catch (Throwable ignored) {
		}
	}
}
