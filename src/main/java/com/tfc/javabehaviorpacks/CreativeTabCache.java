package com.tfc.javabehaviorpacks;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class CreativeTabCache {
	public static final ItemGroup bedrockBlocks = FabricItemGroupBuilder.create(new Identifier("java_datapacks:bedrock_blocks")).icon(() -> new ItemStack(Items.GRASS_BLOCK)).build();
}
