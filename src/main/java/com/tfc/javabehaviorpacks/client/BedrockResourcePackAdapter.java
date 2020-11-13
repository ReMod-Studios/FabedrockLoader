package com.tfc.javabehaviorpacks.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.tfc.javabehaviorpacks.JavaBehaviorPacks;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

//TODO
public class BedrockResourcePackAdapter implements ResourcePack {
	private static InputStream icon;
	
	@Override
	public InputStream openRoot(String fileName) throws IOException {
		icon = BedrockResourcePackAdapter.class.getClassLoader().getResourceAsStream("assets/java-behavior-packs/bedrock.png");
		return icon;
	}
	
	@Override
	public InputStream open(ResourceType type, Identifier id) throws IOException {
		return null;
	}
	
	@Override
	public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter) {
		return ImmutableList.of();
	}
	
	@Override
	public boolean contains(ResourceType type, Identifier id) {
		return false;
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
		} catch (Throwable ignored) {
		}
	}
}
