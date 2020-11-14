package com.tfc.javabehaviorpacks.utils.assets_helpers;

import com.tfc.javabehaviorpacks.JavaBehaviorPacks;

import java.io.IOException;
import java.io.InputStream;

public class Langificator {
	public static String toJson(String bedrock) {
		StringBuilder langFile = new StringBuilder("{\n");
		
		for (String line : bedrock.split("\n")) {
			if (line.contains("=")) {
				String[] entry = line.split("=");
				langFile.append("\t\"").append(
						entry[0].replace(":", ".")
				).append("\": \"").append(
						entry[1]
				).append("\",\n");
			}
		}
		
		langFile.append("\t\"\":\"\"\n" + "}");
		
		return langFile.toString();
	}
	
	public static String toJson(InputStream stream) throws IOException {
		String bedrock = JavaBehaviorPacks.readStream(stream);
		stream.close();
		
		StringBuilder langFile = new StringBuilder("{\n");
		
		for (String line : bedrock.split("\n")) {
			if (line.contains("=")) {
				String[] entry = line.split("=");
				langFile.append("\t\"").append(
						entry[0].replace(":", ".")
				).append("\": \"").append(
						entry[1]
				).append("\",\n");
			}
		}
		
		langFile.append("\t\"\":\"\"\n" + "}");
		
		return langFile.toString();
	}
}
