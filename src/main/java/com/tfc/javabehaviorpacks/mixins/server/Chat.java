package com.tfc.javabehaviorpacks.mixins.server;

import com.tfc.javabehaviorpacks.server.McFunctionExecutor;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Consumer;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class Chat {
	@Shadow public ServerPlayerEntity player;
	
	@Shadow protected abstract void method_31286(String string);
	
	@Shadow protected abstract void filterText(String text, Consumer<String> consumer);
	
	/**
	 * @author
	 */
	@Overwrite
	public void onGameMessage(ChatMessageC2SPacket packet) {
		String string = StringUtils.normalizeSpace(packet.getChatMessage());
		if (string.startsWith("/")) {
			NetworkThreadUtils.forceMainThread(packet, (ServerPlayNetworkHandler) (Object) this, this.player.getServerWorld());
			this.method_31286(string);
		} else if (string.startsWith("!")) {
			if (string.startsWith("!function_bedrock")) {
				McFunctionExecutor.execute(string.substring("!function_bedrock ".length()).replace("$","\n"), this.player);
			} else {
				this.filterText(string, this::method_31286);
			}
		} else {
			this.filterText(string, this::method_31286);
		}
	}
}
