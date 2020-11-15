package com.tfc.javabehaviorpacks;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tfc.javabehaviorpacks.registry_handlers.Block;
import com.tfc.javabehaviorpacks.registry_handlers.Item;
import com.tfc.javabehaviorpacks.utils.BiProvider;
import com.tfc.javabehaviorpacks.utils.PropertiesReader;
import com.tfc.javabehaviorpacks.utils.assets_helpers.Langificator;
import com.tfc.javabehaviorpacks.utils.assets_helpers.NoValidationIdentifier;
import com.tfc.javabehaviorpacks.utils.assets_helpers.BedrockMapper;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class JavaBehaviorPacks implements ModInitializer {
	public static ArrayList<String> namespaces = new ArrayList<>();
	private static final String templateItemJson;
	private static final String templateBlockCubeAll;
	private static final String templateCubeTBS;
	private static final String genericBlockState;
	private static final String blockItemModel;
	public static ArrayList<BiProvider<NoValidationIdentifier, String>> clientItemJsons = new ArrayList<>();
	public static ArrayList<BiProvider<NoValidationIdentifier, String>> clientBlockJsons = new ArrayList<>();
	public static ArrayList<BiProvider<NoValidationIdentifier, String>> clientLangs = new ArrayList<>();
	public static ArrayList<BiProvider<NoValidationIdentifier, File>> clientTextures = new ArrayList<>();
	
	public static ArrayList<BiProvider<String, File>> serverMcFuncs = new ArrayList<>();
	
	public static MinecraftServer server;
	
	static {
		try {
			InputStream stream = JavaBehaviorPacks.class.getClassLoader().getResourceAsStream("assets/java-behavior-packs/item_model.json");
			assert stream != null;
			templateItemJson = readStream(stream);
			stream.close();
			
			stream = JavaBehaviorPacks.class.getClassLoader().getResourceAsStream("assets/java-behavior-packs/block_models/cube_all.json");
			assert stream != null;
			templateBlockCubeAll = readStream(stream);
			stream.close();
			
			stream = JavaBehaviorPacks.class.getClassLoader().getResourceAsStream("assets/java-behavior-packs/block_models/cube_tbs.json");
			assert stream != null;
			templateCubeTBS = readStream(stream);
			stream.close();
			
			stream = JavaBehaviorPacks.class.getClassLoader().getResourceAsStream("assets/java-behavior-packs/block_models/generic_state.json");
			assert stream != null;
			genericBlockState = readStream(stream);
			stream.close();
			
			stream = JavaBehaviorPacks.class.getClassLoader().getResourceAsStream("assets/java-behavior-packs/block_models/block_item.json");
			assert stream != null;
			blockItemModel = readStream(stream);
			stream.close();
		} catch (Throwable err) {
			throw new RuntimeException(err);
		}
	}
	
	public static String readStream(InputStream stream) throws IOException {
		byte[] bytes = new byte[stream.available()];
		stream.read(bytes);
		return new String(bytes);
	}
	
	public static String readFile(File file) throws IOException {
		FileInputStream stream = new FileInputStream(file);
		String output = readStream(stream);
		stream.close();
		return output;
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
	
	public static void writeString(File f, String text) throws IOException {
		if (!f.exists()) {
			f.getParentFile().mkdirs();
			f.createNewFile();
		}
		
		FileOutputStream writer = new FileOutputStream(f);
		writer.write(text.getBytes());
		writer.close();
	}
	
	@Override
	public void onInitialize() {
//		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
//			dispatcher.register(
//					CommandManager.literal("function_bedrock").executes(
//							context ->
//									McFunctionExecutor.execute(
//											context.getInput()
////										.substring("execute_bedrock".length())
//													.replace("$", "")
//									)
//					)
//			);
//		});
		
		CommandUpdater.main(new String[0]);
		
		File bevPacks = new File("behavior_packs");
		if (!bevPacks.exists()) {
			bevPacks.mkdirs();
		}
		StringBuilder packDiscriminatorText = new StringBuilder();
		
		boolean outputPack = false;
		
		try {
			File config = new File("config/java-behavior-packs.properties");
			if (!config.exists()) {
				config.getParentFile().mkdirs();
				config.createNewFile();
				writeString(config, "" +
						"#useful if you want the structure for overwriting the behavior pack loader resources\n" +
						"OutputPack=false\n" +
						"");
			}
			PropertiesReader reader = new PropertiesReader(config);
			outputPack = Boolean.parseBoolean(reader.getValue("OutputPack"));
		} catch (Throwable ignored) {
		}
		
		try {
			String lang = Langificator.toJson(
					JavaBehaviorPacks.class.getClassLoader().getResourceAsStream("assets/java-behavior-packs/builtin/en_US.lang")
			);
			clientLangs.add(BiProvider.of(new NoValidationIdentifier(
							"behaviorpack_base" + packDiscriminatorText, "lang/" + "en_US.lang".toLowerCase().replace(".lang", ".json")
					), lang
			));
			
			if (outputPack) {
				File f = new File(
						"bedrock_resource_pack/assets/" + ("behaviorpack_base") + "/" +
								"lang/" + "en_US.lang".toLowerCase().replace(".lang", ".json")
				);
				writeString(f, lang);
			}
		} catch (Throwable ignored) {
			ignored.printStackTrace();
		}
		
		if (outputPack) {
			try {
				File packMcMeta = new File("bedrock_resource_pack/pack.mcmeta");
				writeString(packMcMeta, "{\n" +
						"   \"pack\": {\n" +
						"      \"pack_format\": 6,\n" +
						"      \"description\": \"Bedrock Assets Output\"\n" +
						"   }\n" +
						"}");
			} catch (Throwable ignored) {
			}
		}
		
		try {
			for (File pack : bevPacks.listFiles()) {
				boolean isServerSide = false;
				boolean isClientSidePack = false;
				try {
					File manifest = new File(pack + "/manifest.json");
					Scanner sc = new Scanner(manifest);
					while (sc.hasNextLine()) {
						String line = sc.nextLine();
						isServerSide = isServerSide || line.contains("data");
						isClientSidePack = isClientSidePack || line.contains("resources");
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
						if (resource.getName().equals("functions")) {
							ArrayList<File> allFuncs = new ArrayList<>();
							getAllFiles(resource, allFuncs);
							for (File item : allFuncs) {
								if (item.getName().endsWith(".mcfunction")) {
									serverMcFuncs.add(BiProvider.of(item.getPath().substring(pack.getPath().length()),item));
								}
							}
						}
						if (resource.getName().equals("jbp-client")) {
							try {
								handleClient(pack, outputPack, packDiscriminatorText);
							} catch (Throwable ignored) {
							}
						}
					}
				}
				if (isClientSidePack) {
					try {
						handleClient(pack, outputPack, packDiscriminatorText);
					} catch (Throwable ignored) {
					}
				}
			}
		} catch (Throwable err) {
			err.printStackTrace();
		}
		
		CreativeTabCache.initTabs();
	}
	
	public void handleClient(File pack, boolean outputPack, StringBuilder packDiscriminatorText) throws IOException {
		for (File resource : pack.listFiles()) {
			Gson gson = new Gson();
			
			FileInputStream stream = new FileInputStream(new File(pack + "/textures/item_texture.json"));
			JsonObject textureMap = gson.fromJson(readStream(stream), JsonObject.class).getAsJsonObject("texture_data");
			stream.close();
			
			if (resource.getName().equals("texts")) {
				ArrayList<File> allLangs = new ArrayList<>();
				getAllFiles(resource, allLangs);
				for (File lang : allLangs) {
					if (lang.getName().endsWith(".lang")) {
						StringBuilder langFile = new StringBuilder("{\n");
						
						Scanner sc = new Scanner(lang);
						
						while (sc.hasNextLine()) {
							String line = sc.nextLine();
							if (line.contains("=")) {
								String[] entry = line.split("=");
								langFile.append("\t\"").append(
										entry[0].replace(":", ".")
								).append("\": \"").append(
										entry[1]
								).append("\",\n");
							}
						}
						
						sc.close();
						
						langFile.append("\t\"\":\"\"\n" + "}");
						
						File f = new File(
								"bedrock_resource_pack/assets/" + ("bedrock" + packDiscriminatorText) + "/" +
										"lang/" + lang.getName().toLowerCase().replace(".lang", ".json")
						);
						
						if (outputPack) {
							writeString(f, langFile.toString());
						}
						
						clientLangs.add(BiProvider.of(new NoValidationIdentifier(
										"bedrock" + packDiscriminatorText, "lang/" + lang.getName().toLowerCase().replace(".lang", ".json")
								), langFile.toString()
										.replace(".name", "")
										.replace("tile.", "block.")
						));
						
						namespaces.add("bedrock" + packDiscriminatorText);
						
						packDiscriminatorText.append("_");
					}
				}
			} else if (resource.getName().equals("items")) {
				ArrayList<File> allItems = new ArrayList<>();
				getAllFiles(resource, allItems);
				for (File item : allItems) {
					StringBuilder s = new StringBuilder();
					Scanner sc = new Scanner(item);
					
					while (sc.hasNextLine()) {
						String line = sc.nextLine();
						//Curse you too bridge., lol
						if (!line.startsWith("//")) {
							s.append(line.trim());
						}
					}
					
					sc.close();
					
					JsonObject object = gson.fromJson(s.toString(), JsonObject.class);
					JsonObject itemJsonObj = object.getAsJsonObject("minecraft:item");
					JsonObject componentJsonObj = itemJsonObj.getAsJsonObject("components");
					JsonObject descriptionJsonObj = itemJsonObj.getAsJsonObject("description");
					
					String id = descriptionJsonObj.getAsJsonPrimitive("identifier").getAsString();
					String icon = componentJsonObj.getAsJsonPrimitive("minecraft:icon").getAsString();
					String renderOffsets = componentJsonObj.getAsJsonPrimitive("minecraft:render_offsets").getAsString();
					
					if (componentJsonObj.has("minecraft:creative_category")) {
						String tab = componentJsonObj.getAsJsonObject("minecraft:creative_category").getAsJsonPrimitive("parent").getAsString();
						CreativeTabCache.addItemToTab(tab, id);
					}
					
					if (descriptionJsonObj.has("category")) {
						String tab =
								descriptionJsonObj
										.getAsJsonPrimitive("category")
										.getAsString();
						CreativeTabCache.addItemToTab(tab, id);
					}
					
					NoValidationIdentifier identifier =
							new NoValidationIdentifier(
									(new NoValidationIdentifier(id).getNamespace()),
									("models/item/" + new NoValidationIdentifier(id).getPath() + ".json")
							);
					
					NoValidationIdentifier identifierTexture =
							new NoValidationIdentifier(
									new NoValidationIdentifier(id).getNamespace(), ""
							);
					
					String texture = textureMap
							.getAsJsonObject(icon)
							.getAsJsonArray("textures")
							.get(0)
							.getAsString();
					
					String texture1 = identifier.getNamespace() + ":" + texture;
					
					String newJson = templateItemJson.replace(
							"%texture%", texture1.replace("textures/", "").toLowerCase()
					).replace(
							"%render_offsets%",
							BedrockMapper.getMappedForItem(renderOffsets)
					);
					
					File f = new File(
							"bedrock_resource_pack/assets/" +
									identifier.toString().replace(":", "/")
					);
					
					if (outputPack) {
						writeString(f, newJson);
					}
					
					clientItemJsons.add(
							BiProvider.of(
									identifier,
									newJson
							)
					);

					File file = new File(pack.toString() + "/" + new NoValidationIdentifier(texture1).getPath() + ".png");

					clientTextures.add(
							BiProvider.of(
									new NoValidationIdentifier(
											identifier.getNamespace(), identifierTexture.getPath() + (texture + ".png").toLowerCase()
									),
									file
							)
					);

					f = new File(
							"bedrock_resource_pack/assets/" +
									"/" + texture1.replace(":", "/") + ".png"
					);

					if (outputPack) {
						if (!f.exists()) {
							f.getParentFile().mkdirs();
							f.createNewFile();
						}

						Files.copy(file, f);
					}
				}
			} else if (resource.getName().endsWith("blocks.json")) {
				try {
					InputStream terrain = new FileInputStream(new File(pack+"/textures/terrain_texture.json"));
					JsonObject blockTextureMap = gson.fromJson(readStream(terrain),JsonObject.class).getAsJsonObject("texture_data");
					terrain.close();
					
					FileInputStream stream1 = new FileInputStream(resource);
					JsonObject blocks = gson.fromJson(readStream(stream1), JsonObject.class);
					stream1.close();
					
					blocks.entrySet().forEach(entry -> {
						try {
							if (!entry.getKey().equals("format_version")) {
								NoValidationIdentifier identifier = new NoValidationIdentifier(entry.getKey());
								JsonElement textures;
								
								try {
									textures = entry.getValue().getAsJsonObject().getAsJsonPrimitive("textures");
								} catch (Throwable ignored) {
									textures = entry.getValue().getAsJsonObject().getAsJsonObject("textures");
								}
								
								ArrayList<String> allTextures = new ArrayList<>();
								String model = "";
								
								if (textures != null) {
									if (textures.isJsonPrimitive()) {
										System.out.println(textures.getAsString());
										String tex = blockTextureMap
												.getAsJsonObject(textures.getAsJsonPrimitive().getAsString())
												.getAsJsonArray("textures")
												.get(0)
												.getAsString()
												.replace("textures/","");
										model = templateBlockCubeAll.replace("%texture%", identifier.getNamespace()+":"+tex.toLowerCase());
										allTextures.add(tex);
									} else {
										JsonObject textureObject = (JsonObject)textures;
										if (
												textureObject.has("up") &&
														textureObject.has("down") &&
														textureObject.has("side")
										) {
											model = templateCubeTBS;
											BiProvider<String, String>[] texturesArray = new BiProvider[]{
													BiProvider.of("up", textureObject.getAsJsonPrimitive("up").getAsString()),
													BiProvider.of("down", textureObject.getAsJsonPrimitive("down").getAsString()),
													BiProvider.of("side", textureObject.getAsJsonPrimitive("side").getAsString())
											};
											for (BiProvider<String, String> s : texturesArray) {
												System.out.println(s.getT());
												System.out.println(s.getV());
												String tex = blockTextureMap
														.getAsJsonObject(s.getV())
														.getAsJsonArray("textures")
														.get(0)
														.getAsString()
														.replace("textures/","");
												model = model.replace("%" + s.getT() + "%", identifier.getNamespace() + ":" + tex.toLowerCase());
												allTextures.add(tex);
											}
											System.out.println(model);
										}
									}
								}
								
								if (!model.equals("")) {
									String state = genericBlockState.replace("%namespace%", identifier.getNamespace()).replace("%block%", identifier.getPath());
									
									String item = blockItemModel.replace("%namespace%",identifier.getNamespace()).replace("%block%",identifier.getPath());
									
									clientBlockJsons.add(BiProvider.of(
											new NoValidationIdentifier(identifier.getNamespace(), "blockstates/" + identifier.getPath() + ".json"),
											state
									));
									
									clientBlockJsons.add(BiProvider.of(
											new NoValidationIdentifier(identifier.getNamespace(), "models/block/" + identifier.getPath() + ".json"),
											model
									));
									
									clientItemJsons.add(BiProvider.of(
											new NoValidationIdentifier(identifier.getNamespace(), "models/item/" + identifier.getPath() + ".json"),
											item
									));
									
									for (String s : allTextures) {
//										NoValidationIdentifier identifierTexture = new NoValidationIdentifier(identifier.getNamespace(), s);
//
//										File file = new File(pack.toString() + "/textures/"+packBlockFolder+"/" + identifierTexture.getPath() + ".png");
//
										System.out.println(s);
										NoValidationIdentifier identifierTexture = new NoValidationIdentifier(identifier.getNamespace(), "textures/"+s+".png");
										
//										f = new File(
//												"bedrock_resource_pack/assets/" + identifier.getNamespace() +
//														"/textures/" + identifierTexture.getPath() + ".png"
//										);
//
//										if (!f.exists()) {
//											f.getParentFile().mkdirs();
//											f.createNewFile();
//										}
										
										File file = new File(pack.toString() + "/" + identifierTexture.getPath());
										
										clientTextures.add(
												BiProvider.of(
														identifierTexture,
														file
												)
										);
									}
									
									if (outputPack) {
										try {
											File f = new File(
													"bedrock_resource_pack/assets/" + identifier.getNamespace() +
															"/blockstates/" + identifier.getPath() + ".json"
											);
											
											writeString(f, state);
											
											f = new File(
													"bedrock_resource_pack/assets/" + identifier.getNamespace() +
															"/models/block/" + identifier.getPath() + ".json"
											);
											
											writeString(f, model);
											
											f = new File(
													"bedrock_resource_pack/assets/" + identifier.getNamespace() +
															"/models/item/" + identifier.getPath() + ".json"
											);
											
											writeString(f, item);
											
											for (String s : allTextures) {
												NoValidationIdentifier identifierTexture = new NoValidationIdentifier(identifier.getNamespace(), "textures/"+s+".png");
												
												f = new File(
														"bedrock_resource_pack/assets/" + identifier.getNamespace() +
																"/textures/" + identifierTexture.getPath() + ".png"
												);
												
												if (!f.exists()) {
													f.getParentFile().mkdirs();
													f.createNewFile();
												}
												
												File file = new File(pack.toString() + "/textures/" + identifierTexture.getPath() + ".png");
												
												Files.copy(file, f);
											}
										} catch (Throwable ignored) {
										}
									}
								}
							}
						} catch (Throwable err) {
							err.printStackTrace();
						}
					});
				} catch (Throwable err) {
					err.printStackTrace();
				}
			}
		}
	}
}
