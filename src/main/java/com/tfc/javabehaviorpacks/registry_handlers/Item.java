package com.tfc.javabehaviorpacks.registry_handlers;

import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import org.json.JSONObject;

import java.io.File;
import java.util.Scanner;

public class Item {
	public static void register(File item) {
		try {
			StringBuilder s = new StringBuilder();
			Scanner sc = new Scanner(item);
			
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
//									.get("\"minecraft:item\"")
//									.get("\"description\"")
//									.get("\"identifier\"").getHeld()
//							);
			
			JSONObject object = new JSONObject(s.toString());
			
			int maxStack = 64;
			int useTime = 0;
			
			FoodComponent.Builder food = null;
			
			JSONObject itemObj = object.getJSONObject("minecraft:item");
			
			if (itemObj.has("components")) {
				JSONObject components = itemObj.getJSONObject("components");
				if (components.has("minecraft:max_stack_size")) {
					maxStack = components.getInt("minecraft:max_stack_size");
				}
				if (components.has("minecraft:use_duration")) {
					useTime = components.getInt("minecraft:use_duration");
				}
				if (components.has("minecraft:food")) {
					JSONObject foodDescription = components.getJSONObject("minecraft:food");
					food = new FoodComponent.Builder();
					if (foodDescription.has("nutrition")) {
						food = food.hunger(foodDescription.getInt("nutrition"));
					}
					if (foodDescription.has("saturation_modifier")) {
						//TODO
						food = food.saturationModifier(0);
					}
					if (foodDescription.has("can_always_eat")) {
						if (foodDescription.getBoolean("can_always_eat")) {
							food = food.alwaysEdible();
						}
					}
				}
			}
			
			String id = itemObj.getJSONObject("description").getString("identifier");
			
			System.out.println(id);
			
			net.minecraft.item.Item.Settings settings = new net.minecraft.item.Item.Settings().maxCount(maxStack);
			
			if (food != null) {
				settings = settings.food(food.build());
			}
			
			final int finalUseTime = useTime;
			Registry.register(Registry.ITEM, id, new net.minecraft.item.Item(settings) {
				@Override
				public int getMaxUseTime(ItemStack stack) {
					return finalUseTime;
				}
			});
		} catch (Throwable ignored) {
			ignored.printStackTrace();
		}
	}
}
