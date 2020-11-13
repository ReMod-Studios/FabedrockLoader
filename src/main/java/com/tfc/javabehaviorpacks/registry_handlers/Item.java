package com.tfc.javabehaviorpacks.registry_handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tfc.javabehaviorpacks.EnumFoodQuality;
import com.tfc.javabehaviorpacks.JavaBehaviorPacks;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.util.Objects;
import java.util.Scanner;

//https://bedrock.dev/docs/stable/Item
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
			
			Gson gson = new Gson();
			
			JsonObject object = gson.fromJson(s.toString(), JsonObject.class);
			
			int maxStack = 64;
			int useTime = 0;
			
			FoodComponent.Builder food = null;
			
			JsonObject itemObj = object.getAsJsonObject("minecraft:item");
			
			if (itemObj.has("components")) {
				JsonObject components = itemObj.getAsJsonObject("components");
				if (components.has("minecraft:max_stack_size")) {
					maxStack = components.getAsJsonPrimitive("minecraft:max_stack_size").getAsInt();
				}
				if (components.has("minecraft:use_duration")) {
					useTime = components.getAsJsonPrimitive("minecraft:use_duration").getAsInt();
				}
				if (components.has("minecraft:food")) {
					JsonObject foodDescription = components.getAsJsonObject("minecraft:food");
					food = new FoodComponent.Builder();
					if (foodDescription.has("nutrition")) {
						food = food.hunger(foodDescription.getAsJsonPrimitive("nutrition").getAsInt());
					}
					if (foodDescription.has("saturation_modifier")) {
						food = food.saturationModifier(Objects.requireNonNull(EnumFoodQuality.forName(
								foodDescription.getAsJsonPrimitive("saturation_modifier").getAsString()
						)).getVal());
					}
					if (foodDescription.has("can_always_eat")) {
						if (foodDescription.getAsJsonPrimitive("can_always_eat").getAsBoolean()) {
							food = food.alwaysEdible();
						}
					}
				}
			}
			
			String id = itemObj.getAsJsonObject("description").getAsJsonPrimitive("identifier").getAsString();
			
			System.out.println(id);
			
			net.minecraft.item.Item.Settings settings = new net.minecraft.item.Item.Settings().maxCount(maxStack);
			
			if (food != null) {
				settings = settings.food(food.build());
			}
			
			if (!JavaBehaviorPacks.namespaces.contains(new Identifier(id).getNamespace()))
				JavaBehaviorPacks.namespaces.add(new Identifier(id).getNamespace());
			
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
