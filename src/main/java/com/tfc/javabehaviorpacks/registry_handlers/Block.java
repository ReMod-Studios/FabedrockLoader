package com.tfc.javabehaviorpacks.registry_handlers;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import org.json.JSONObject;

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
				//Curse you too bridge, lol
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
			
			JSONObject object = new JSONObject(s.toString());
			
			JSONObject blockObj = object.getJSONObject("minecraft:block");
			
			String color = "#0";
			
			if (blockObj.has("components")) {
				JSONObject components = blockObj.getJSONObject("components");
				if (components.has("minecraft:map_color")) {
					color = components.getString("minecraft:map_color");
				}
			}
			
			String id = blockObj.getJSONObject("description").getString("identifier");
			
			System.out.println(id);
			
			AbstractBlock.Settings settings = AbstractBlock.Settings.of(Material.STONE);
			
			Registry.register(Registry.BLOCK, id, new net.minecraft.block.Block(settings));
		} catch (Throwable ignored) {
			ignored.printStackTrace();
		}
	}
}
