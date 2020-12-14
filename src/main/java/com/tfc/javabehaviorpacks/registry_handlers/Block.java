package com.tfc.javabehaviorpacks.registry_handlers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tfc.javabehaviorpacks.CreativeTabCache;
import com.tfc.javabehaviorpacks.JavaBehaviorPacks;
import com.tfc.javabehaviorpacks.server.McFunctionExecutor;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

//https://bedrock.dev/docs/stable/Blocks
public class Block {
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
			
			//This might be impossible to implement, sadly
			String color = "#0";
			
			float destroy_time = 0;
			float explosion_resistance = 0;
			
			boolean isSolid = false;
			
			String[] commandsOnClick = new String[0];
			String[] commandsStep = new String[0];
			String clickTarg = "";
			String stepTarg = "";
			
			if (blockObj.has("components")) {
				JsonObject components = blockObj.getAsJsonObject("components");
				try {
					if (components.has("minecraft:map_color")) {
						color = components.getAsJsonPrimitive("minecraft:map_color").getAsString();
					}
				} catch (Throwable ignored) {
				}
				if (components.has("minecraft:destroy_time")) {
					destroy_time = components.getAsJsonPrimitive("minecraft:destroy_time").getAsInt();
				}
				if (components.has("minecraft:explosion_resistance")) {
					explosion_resistance = components.getAsJsonPrimitive("minecraft:explosion_resistance").getAsInt();
				}
				if (components.has("minecraft:breathability")) {
					isSolid = components.getAsJsonPrimitive("minecraft:breathability").getAsString().equals("solid");
				}
				if (components.has("minecraft:on_interact")) {
					JsonObject interact = components.getAsJsonObject("minecraft:on_interact");
					if (interact.has("run_command")) {
						JsonArray commands = interact.getAsJsonObject("run_command").getAsJsonArray("command");
						ArrayList<String> cmds = new ArrayList<>();
						for (JsonElement cmd : commands) {
							try {
								cmds.add(cmd.getAsJsonPrimitive().getAsString());
							} catch (Throwable ignored) {
							}
						}
						commandsOnClick = cmds.toArray(new String[0]);
					}
					if (interact.has("target")) {
						clickTarg = interact.getAsJsonPrimitive("target").getAsString();
					}
				}
				if (components.has("minecraft:on_step_on")) {
					JsonObject interact = components.getAsJsonObject("minecraft:on_step_on");
					if (interact.has("run_command")) {
						JsonArray commands = interact.getAsJsonObject("run_command").getAsJsonArray("command");
						ArrayList<String> cmds = new ArrayList<>();
						for (JsonElement cmd : commands) {
							try {
								cmds.add(cmd.getAsJsonPrimitive().getAsString());
							} catch (Throwable ignored) {
							}
						}
						commandsStep = cmds.toArray(new String[0]);
					}
					if (interact.has("target")) {
						stepTarg = interact.getAsJsonPrimitive("target").getAsString();
					}
				}
			}
			
			String id = blockObj.getAsJsonObject("description").getAsJsonPrimitive("identifier").getAsString();
			
//			System.out.println(id);
			
			if (!JavaBehaviorPacks.namespaces.contains(new Identifier(id).getNamespace()))
				JavaBehaviorPacks.namespaces.add(new Identifier(id).getNamespace());
			
			boolean finalIsSolid = isSolid;
			AbstractBlock.Settings settings = AbstractBlock.Settings.of(Material.STONE)
					.strength(destroy_time, explosion_resistance)
					.suffocates((state, world, pos) -> finalIsSolid);
			
			String[] finalCommandsOnClick = commandsOnClick;
			String finalTarg = clickTarg;
			String finalStepTarg = stepTarg;
			String[] finalCommandsStep = commandsStep;
			net.minecraft.block.Block bk = Registry.register(Registry.BLOCK, id, new net.minecraft.block.Block(settings) {
				@Override
				public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
					if (finalCommandsOnClick.length == 0) {
						return super.onUse(state, world, pos, player, hand, hit);
					} else {
						if (!world.isClient) {
							if (hand.equals(Hand.MAIN_HAND)) {
								for (String s : finalCommandsOnClick) {
									if (finalTarg.equals("self")) {
										McFunctionExecutor.execute(s);
									} else {
										McFunctionExecutor.execute(s,player);
									}
								}
							}
						}
						return ActionResult.SUCCESS;
					}
				}
				
				//TODO: make this only call the commands when the player walks onto the block
				@Override
				public void onSteppedOn(World world, BlockPos pos, Entity entity) {
					super.onSteppedOn(world, pos, entity);
					if (finalCommandsStep.length != 0) {
						if (!world.isClient) {
							for (String s : finalCommandsStep) {
								if (finalStepTarg.equals("self")) {
									McFunctionExecutor.execute(s);
								} else {
									McFunctionExecutor.execute(s, entity);
								}
							}
						}
					}
				}
			});
			Registry.register(Registry.ITEM, id, new BlockItem(bk, new Item.Settings().group(CreativeTabCache.bedrockBlocks)));
		} catch (Throwable ignored) {
			ignored.printStackTrace();
		}
	}
}
