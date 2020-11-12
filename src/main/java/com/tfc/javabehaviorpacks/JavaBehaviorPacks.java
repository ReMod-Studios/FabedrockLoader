package com.tfc.javabehaviorpacks;

import com.tfc.javabehaviorpacks.json.JsonObject;
import com.tfc.javabehaviorpacks.json.JsonReader;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import org.json.JSONObject;

import java.io.File;
import java.util.Scanner;

public class JavaBehaviorPacks implements ModInitializer {
	@Override
	public void onInitialize() {
		File bevPacks = new File("behavior_packs");
		for (File pack : bevPacks.listFiles()) {
			for (File resource : pack.listFiles()) {
				if (resource.getName().equals("items")) {
					for (File item : resource.listFiles()) {
						try {
							String s = new String();
							Scanner sc = new Scanner(item);
							
							while (sc.hasNextLine()) {
								String line = sc.nextLine();
								//Curse you too bridge, lol
								if (!line.startsWith("//")) {
									s += line.trim();
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
							
							JSONObject object = new JSONObject(s);
							
//							JsonObject<JsonObject<?>> itemJSON = new JsonObject<>();
//							itemJSON.queueOrCreate("\"format_version\"");
//							itemJSON.queueOrCreate("\"1.16.0\"");
//							itemJSON.queueOrCreate("\"minecraft:item\"");
//							itemJSON.queueOrCreate("{\t\"description\": {\t\t\"identifier\": \"foodstuffs:honey_melon\"\t},\t\"components\": {\t\t\"minecraft:use_duration\": 32,\t\t\"minecraft:food\": {\t\t\t\"nutrition\": 3,\t\t\t\"saturation_modifier\": \"good\"\t\t},\t\t\"minecraft:max_stack_size\": 64,\t\t\"minecraft:hand_equipped\": false,\t\t\"minecraft:stacked_by_data\": true,\t\t\"minecraft:foil\": false\t}}".replace("\t","").replace("\n",""));
//
//							System.out.println(itemJSON);
							
//							String id = s.substring(s.indexOf("\"identifier\":")).substring("\"identifier\":".length()).trim();
//							id = id.substring(1,id.indexOf("\""));
							
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
										food.hunger(foodDescription.getInt("nutrition"));
									}
									if (foodDescription.has("saturation_modifier")) {
										food.saturationModifier(0);
									}
								}
							}
							
							String id = itemObj.getJSONObject("description").getString("identifier");
							
							System.out.println(id);

							Item.Settings settings = new Item.Settings().maxCount(maxStack);
							
							if (food != null) {
								settings = settings.food(food.build());
							}
							
							final int finalUseTime = useTime;
							Registry.register(Registry.ITEM, id, new Item(settings) {
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
			}
		}
	}
}
