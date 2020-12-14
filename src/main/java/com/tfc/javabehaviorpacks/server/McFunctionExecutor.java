package com.tfc.javabehaviorpacks.server;

import com.tfc.javabehaviorpacks.CommandUpdater;
import com.tfc.javabehaviorpacks.JavaBehaviorPacks;
import com.tfc.javabehaviorpacks.utils.BiProvider;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;

import java.io.File;

public class McFunctionExecutor {
	public static int execute(String func) {
		boolean isOutput = JavaBehaviorPacks.server.getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK);
		JavaBehaviorPacks.server.getGameRules().get(GameRules.SEND_COMMAND_FEEDBACK).set(false,JavaBehaviorPacks.server);
		int executed = 0;
		for (String s : func.split("\n")) {
			s = s.trim();
			if (!s.startsWith("#") && !s.equals("")) {
				if (s.startsWith("function")) {
					try {
						boolean hasRun = false;
						for (BiProvider<String, File> functions : JavaBehaviorPacks.serverMcFuncs) {
							
							String text = functions.getT().substring("\\functions\\".length(),functions.getT().length()-".mcfunction".length()).replace("\\","/");
							System.out.println(text);
							if (text.equals(s.substring("function ".length()))) {
								executed +=
										execute(
												JavaBehaviorPacks.readFile(
														functions.getV()
												)
										);
								hasRun = true;
							}
						}
						System.out.println(hasRun);
					} catch (Throwable ignored) {
					}
				} else {
					JavaBehaviorPacks.server.getCommandManager().execute(
							JavaBehaviorPacks.server.getCommandSource(),
							CommandUpdater.update(s)
					);
					executed++;
				}
			}
		}
		JavaBehaviorPacks.server.getGameRules().get(GameRules.SEND_COMMAND_FEEDBACK).set(isOutput,JavaBehaviorPacks.server);
		return executed;
	}
	
	public static int execute(String func, BlockPos pos) {
		boolean isOutput = JavaBehaviorPacks.server.getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK);
		JavaBehaviorPacks.server.getGameRules().get(GameRules.SEND_COMMAND_FEEDBACK).set(false,JavaBehaviorPacks.server);
		int executed = 0;
		for (String s : func.split("\n")) {
			s = s.trim();
			if (!s.startsWith("#") && !s.equals("")) {
				if (s.startsWith("function")) {
					try {
						boolean hasRun = false;
						for (BiProvider<String, File> functions : JavaBehaviorPacks.serverMcFuncs) {
							
							String text = functions.getT().substring("\\functions\\".length(),functions.getT().length()-".mcfunction".length()).replace("\\","/");
							System.out.println(text);
							if (text.equals(s.substring("function ".length()))) {
								executed +=
										execute(
												JavaBehaviorPacks.readFile(
														functions.getV()
												)
										);
								hasRun = true;
							}
						}
						System.out.println(hasRun);
					} catch (Throwable ignored) {
					}
				} else {
					JavaBehaviorPacks.server.getCommandManager().execute(
							JavaBehaviorPacks.server.getCommandSource().withPosition(new Vec3d(pos.getX(),pos.getY(),pos.getZ())),
							CommandUpdater.update(s)
					);
					executed++;
				}
			}
		}
		JavaBehaviorPacks.server.getGameRules().get(GameRules.SEND_COMMAND_FEEDBACK).set(isOutput,JavaBehaviorPacks.server);
		return executed;
	}
	
	public static int execute(String func, Entity executor) {
		boolean isOutput = ((ServerWorld)executor.getEntityWorld()).getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK);
		((ServerWorld)executor.getEntityWorld()).getGameRules().get(GameRules.SEND_COMMAND_FEEDBACK).set(false,((ServerWorld)executor.getEntityWorld()).getServer());
		int executed = 0;
		for (String s : func.split("\n")) {
			s = s.trim();
			if (!s.startsWith("#") && !s.equals("")) {
				if (s.startsWith("function")) {
					try {
						boolean hasRun = false;
						for (BiProvider<String, File> functions : JavaBehaviorPacks.serverMcFuncs) {
							String text = functions.getT().substring("\\functions\\".length(),functions.getT().length()-".mcfunction".length()).replace("\\","/");
							System.out.println(text);
							if (text.equals(s.substring("function ".length()))) {
								executed +=
										execute(
												JavaBehaviorPacks.readFile(
														functions.getV()
												), executor
										);
								hasRun = true;
							}
						}
						System.out.println(hasRun);
					} catch (Throwable ignored) {
					}
				} else {
					try {
						JavaBehaviorPacks.server.getCommandManager().execute(
								JavaBehaviorPacks.server.getCommandSource().withEntity(executor).withPosition(executor.getPos()),
								CommandUpdater.update(s)
						);
						executed++;
					} catch (Throwable ignored) {
					}
				}
			}
		}
		((ServerWorld)executor.getEntityWorld()).getGameRules().get(GameRules.SEND_COMMAND_FEEDBACK).set(isOutput,((ServerWorld)executor.getEntityWorld()).getServer());
		return executed;
	}
}
