package com.tfc.javabehaviorpacks;

import com.tfc.javabehaviorpacks.registry_handlers.Block;
import com.tfc.javabehaviorpacks.registry_handlers.Item;
import net.fabricmc.api.ModInitializer;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class JavaBehaviorPacks implements ModInitializer {
	public static ArrayList<String> namespaces = new ArrayList<>();
	
	@Override
	public void onInitialize() {
		File bevPacks = new File("behavior_packs");
		if (!bevPacks.exists()) {
			bevPacks.mkdirs();
		}
		try {
			for (File pack : bevPacks.listFiles()) {
				boolean isServerSide = false;
				try {
					File manifest = new File(pack + "/manifest.json");
					Scanner sc = new Scanner(manifest);
					while (sc.hasNextLine()) {
						isServerSide = sc.nextLine().contains("data");
						if (isServerSide) break;
					}
					sc.close();
				} catch (Throwable ignored) {
				}
				if (isServerSide) {
					for (File resource : pack.listFiles()) {
						if (resource.getName().equals("items")) {
							ArrayList<File> allItems = new ArrayList<>();
							getAllFiles(resource, allItems);
							for (File item : allItems) {
								Item.register(item);
							}
						}
						if (resource.getName().equals("blocks")) {
							ArrayList<File> allBlocks = new ArrayList<>();
							getAllFiles(resource, allBlocks);
							for (File item : allBlocks) {
								Block.register(item);
							}
						}
					}
				}
			}
		} catch (Throwable ignored) {
		}
	}
	
	public void getAllFiles(File f, ArrayList<File> list) {
		for (File f1 : f.listFiles()) {
			if (f1.isDirectory()) {
				getAllFiles(f1, list);
			} else {
				list.add(f1);
			}
		}
	}
}
