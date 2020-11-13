package com.tfc.javabehaviorpacks;

import com.tfc.javabehaviorpacks.json.JsonObject;
import com.tfc.javabehaviorpacks.json.JsonReader;
import com.tfc.javabehaviorpacks.registry_handlers.Block;
import com.tfc.javabehaviorpacks.registry_handlers.Item;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import org.json.JSONObject;

import java.io.File;
import java.util.Scanner;

public class JavaBehaviorPacks implements ModInitializer {
	@Override
	public void onInitialize() {
		File bevPacks = new File("behavior_packs");
		if (!bevPacks.exists()) {
			bevPacks.mkdirs();
		}
		try {
			for (File pack : bevPacks.listFiles()) {
				for (File resource : pack.listFiles()) {
					if (resource.getName().equals("items")) {
						for (File item : resource.listFiles()) {
							Item.register(item);
						}
					}
					if (resource.getName().equals("blocks")) {
						for (File item : resource.listFiles()) {
							Block.register(item);
						}
					}
				}
			}
		} catch (Throwable ignored) {
		}
	}
}
