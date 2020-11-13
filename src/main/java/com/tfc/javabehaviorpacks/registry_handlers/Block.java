package com.tfc.javabehaviorpacks.registry_handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tfc.javabehaviorpacks.CreativeTabCache;
import com.tfc.javabehaviorpacks.JavaBehaviorPacks;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;

import java.io.File;
import java.util.Scanner;

//https://bedrock.dev/docs/stable/Blocks
public class Block {
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
			
			Gson gson = new Gson();
			
			JsonObject object = gson.fromJson(s.toString(), JsonObject.class);
			
			JsonObject blockObj = object.getAsJsonObject("minecraft:block");
			
			//This might be impossible to implement, sadly
			String color = "#0";
			
			float destroy_time = 0;
			float explosion_resistance = 0;
			
			if (blockObj.has("components")) {
				JsonObject components = blockObj.getAsJsonObject("components");
				if (components.has("minecraft:map_color")) {
					color = components.getAsJsonPrimitive("minecraft:map_color").getAsString();
				}
				if (components.has("minecraft:destroy_time")) {
					destroy_time = components.getAsJsonPrimitive("minecraft:destroy_time").getAsInt();
				}
				if (components.has("minecraft:explosion_resistance")) {
					explosion_resistance = components.getAsJsonPrimitive("minecraft:explosion_resistance").getAsInt();
				}
			}
			
			String id = blockObj.getAsJsonObject("description").getAsJsonPrimitive("identifier").getAsString();
			
			System.out.println(id);
			
			
			if (!JavaBehaviorPacks.namespaces.contains(new Identifier(id).getNamespace()))
				JavaBehaviorPacks.namespaces.add(new Identifier(id).getNamespace());
			
			
			AbstractBlock.Settings settings = AbstractBlock.Settings.of(Material.STONE)
					.strength(destroy_time, explosion_resistance)
					.suffocates(new AbstractBlock.ContextPredicate() {
						@Override
						public boolean test(BlockState state, BlockView world, BlockPos pos) {
							return false;
						}
					});
			
			net.minecraft.block.Block bk = Registry.register(Registry.BLOCK, id, new net.minecraft.block.Block(settings));
			Registry.register(Registry.ITEM, id, new BlockItem(bk, new Item.Settings().group(CreativeTabCache.bedrockBlocks)));
		} catch (Throwable ignored) {
			ignored.printStackTrace();
		}
	}
}
