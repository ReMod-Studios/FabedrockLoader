package com.tfc.javabehaviorpacks.mixins.server;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerLoggedOn {
	@Inject(at = @At("TAIL"),method = "onPlayerConnect(Lnet/minecraft/network/ClientConnection;Lnet/minecraft/server/network/ServerPlayerEntity;)V")
	public void join(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
//		connection.send();
	}
}
