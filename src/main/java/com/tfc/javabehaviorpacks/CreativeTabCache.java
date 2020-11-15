package com.tfc.javabehaviorpacks;

import com.tfc.javabehaviorpacks.utils.BiProvider;
import com.tfc.javabehaviorpacks.utils.assets_helpers.ParentMapper;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.HashMap;

public class CreativeTabCache {
	public static final ItemGroup bedrockBlocks = FabricItemGroupBuilder.create(new Identifier("java_behaviorpacks:bedrock_blocks")).icon(() -> new ItemStack(Items.GRASS_BLOCK)).build();
	public static final ItemGroup bedrockItems = FabricItemGroupBuilder.create(new Identifier("java_behaviorpacks:bedrock_items")).icon(() -> new ItemStack(Items.BLAZE_POWDER)).build();
	
	private static final HashMap<String, BiProvider<FabricItemGroupBuilder, ArrayList<String>>> groupHashMap = new HashMap<>();
	private static final HashMap<ItemGroup, ArrayList<String>> builtGroupHashMap = new HashMap<ItemGroup, ArrayList<String>>();
	
	public static void addItemToTab(String translationKey, String id) {
		try {
			translationKey = translationKey.toLowerCase().substring(0, 1) + translationKey.substring(1);
			
			translationKey = ParentMapper.getMappedForTab(translationKey);
			
			if (!groupHashMap.containsKey(translationKey)) {
				if (translationKey.equals("items_tab") || translationKey.equals("blocks_tab")) {
					return;
				} else {
					groupHashMap.put(
							translationKey,
							BiProvider.of(
									FabricItemGroupBuilder.create(new Identifier(
											"bedrock_tabs",
											translationKey
									)).icon(() -> new ItemStack(Items.BEDROCK)),
									new ArrayList<>())
					);
				}
			}
			
			groupHashMap.get(translationKey).getV().add(id);
		} catch (Throwable err) {
			err.printStackTrace();
		}
	}
	
	public static void initTabs() {
		groupHashMap.forEach((name, group) -> {
					builtGroupHashMap.put(
							group.getT()
//									.appendItems((list) -> {
//										group.getV().forEach(id -> {
//											list.add(new ItemStack(Registry.ITEM.get(new Identifier(id))));
//										});
//									})
									.build(),
							group.getV()
					);
				}
		);
	}
	
	public static ArrayList<ItemGroup> getGroupsFor(ItemStack stack) {
		Identifier id = Registry.ITEM.getId(stack.getItem());
		ArrayList<ItemGroup> groups = new ArrayList<>();
		
		if (stack.getItem() instanceof BlockItem) groups.add(bedrockBlocks);
		else groups.add(bedrockItems);
		
		builtGroupHashMap.forEach(
				(tab, list) -> {
					if (list.contains(id.toString())) {
						groups.add(tab);
					}
				}
		);
		return groups;
	}
}
