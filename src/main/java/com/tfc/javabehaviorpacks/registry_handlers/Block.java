package com.tfc.javabehaviorpacks.registry_handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tfc.javabehaviorpacks.JavaBehaviorPacks;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Material;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.util.Scanner;

public class Block {
	//TODO: do more than just registering it
	public static void register(File block) {
		try {
			StringBuilder s = new StringBuilder();
			Scanner sc = new Scanner(block);
			
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				//Curse you too bridge., lol
				if (!line.startsWith("//")) {
					s.append(line.trim());
				}
			}
			
			sc.close();

//							JsonObject<JsonObject<?>> itemJSON1 = JsonReader.read(s);
//
//							System.out.println(itemJSON1);
//							System.out.println(itemJSON1.get("\"format_version\""));
//							System.out.println(itemJSON1.get("\"format_version\"").getHeld());
//							System.out.println(itemJSON1
//									.get("\"minecraft:block\"")
//									.get("\"description\"")
//									.get("\"identifier\"").getHeld()
//							);
			
			Gson gson = new Gson();
			
			JsonObject object = gson.fromJson(s.toString(), JsonObject.class);
			
			JsonObject blockObj = object.getAsJsonObject("minecraft:block");
			
			String color = "#0";
			
			if (blockObj.has("components")) {
				JsonObject components = blockObj.getAsJsonObject("components");
				if (components.has("minecraft:map_color")) {
					color = components.getAsJsonPrimitive("minecraft:map_color").getAsString();
				}
			}
			
			String id = blockObj.getAsJsonObject("description").getAsJsonPrimitive("identifier").getAsString();
			
			System.out.println(id);
			
			if (!JavaBehaviorPacks.namespaces.contains(new Identifier(id).getNamespace()))
				JavaBehaviorPacks.namespaces.add(new Identifier(id).getNamespace());
			
			AbstractBlock.Settings settings = AbstractBlock.Settings.of(Material.STONE);
			
			Registry.register(Registry.BLOCK, id, new net.minecraft.block.Block(settings));
		} catch (Throwable ignored) {
			ignored.printStackTrace();
		}
	}
}
