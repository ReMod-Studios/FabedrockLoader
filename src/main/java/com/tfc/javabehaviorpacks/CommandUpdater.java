package com.tfc.javabehaviorpacks;

import com.tfc.javabehaviorpacks.utils.assets_helpers.BedrockMapper;

public class CommandUpdater {
	public static void main(String[] args) {
		System.out.println(CommandUpdater.update("execute @a ~ ~ ~ say hi"));
		System.out.println(CommandUpdater.update("execute @a ~ ~ ~ detect ~ ~ ~ stone 0 say hi"));
		
		System.out.println(CommandUpdater.update("execute @a ~ ~ ~ effect @a slow_falling 0"));
		System.out.println(CommandUpdater.update("execute @a ~ ~ ~ detect ~ ~ ~ stone 0 effect @a slow_falling 10 180 true"));
	}
	
	public static String update(String command) {
		String[] args = command.split(" ");
		if (args[0].equals("execute")) return updateExecute(args);
		else if (args[0].equals("effect")) return updateEffect(args);
		else if (args[0].equals("playsound")) return updatePlaySound(args);
		return command;
	}
	
	public static String updateExecute(String[] args) {
		//execute @a ~ ~ ~ detect ~ ~ ~ stone 0 say hi
		//execute as @a at @s positioned ~ ~ ~ if block ~ ~ ~ stone run say hi
		String cmd = args[0] + " as " + args[1] + " at @s positioned " + args[2] + " " + args[3] + " " + args[4] + " ";
		
		int arg = 5;
		if (args[arg].equals("detect")) {
			arg = 11;
			cmd += "if block " + args[6] + " " + args[7] + " " + args[8] + " " + args[9] + " ";
		}
		
		cmd += "run ";
		
		StringBuilder builder = new StringBuilder();
		for (int i = arg; i < args.length; i++) builder.append(args[i]).append(" ");
		cmd += update(builder.toString().trim());

		return cmd;
	}
	
	public static String updateEffect(String[] args) {
		//effect @a slow_falling 100 10 true
		//effect give @a slow_falling 100 10 true
		
		//effect @a slow_falling 0
		//effect take @a slow_falling
		
		if (args.length >= 3 && args[3].equals("0")) {
			return args[0] + " clear " + args[1] + " " + args[2];
		} else {
			String cmd = args[0] + " give " + args[1] + " " + args[2];
			
			for (int i = 3; i < args.length; i++) {
				cmd += " " + args[i];
			}
			
			return cmd;
		}
	}
	
	public static String updatePlaySound(String[] args) {
		String cmd = args[0] + " " + BedrockMapper.getMappedForSound(args[1]);
		
		if (args.length>=3) {
			for (int i=2;i<args.length;i++) {
				cmd+=" "+args[i];
			}
		} else {
			cmd+=" master @a ~ ~ ~";
		}
		
		return cmd;
	}
}
