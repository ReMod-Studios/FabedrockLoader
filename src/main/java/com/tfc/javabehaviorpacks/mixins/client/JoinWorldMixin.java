package com.tfc.javabehaviorpacks.mixins.client;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameJoinS2CPacket.class)
//TODO: make the server send the dp blocks+items to the client for the client to register
public class JoinWorldMixin {
	@Shadow private DynamicRegistryManager.Impl registryManager;
	
	@Inject(at = @At("TAIL"),method = "read(Lnet/minecraft/network/PacketByteBuf;)V")
	public void doRegistries(PacketByteBuf buf, CallbackInfo ci) {
		MutableRegistry<Item> items = registryManager.get(Registry.ITEM.getKey());
		items.forEach((item) -> {
			if (!Registry.BLOCK.containsId(items.getId(item))) {
				Registry.register(Registry.ITEM, items.getId(item).toString(), item);
			}
		});
		MutableRegistry<Block> blocks = registryManager.get(Registry.BLOCK.getKey());
		blocks.forEach((block) -> {
			if (!Registry.BLOCK.containsId(blocks.getId(block))) {
				Registry.register(Registry.BLOCK, blocks.getId(block).toString(), block);
			}
		});
	}
}
