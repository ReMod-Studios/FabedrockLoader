package com.tfc.javabehaviorpacks.mixins.client;

import com.tfc.javabehaviorpacks.client.BedrockResourcePackAdapter;
import net.minecraft.client.resource.ClientBuiltinResourcePackProvider;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ClientBuiltinResourcePackProvider.class)
public class ResourcePackManagerMixin {
	@Inject(at = @At("RETURN"), method = "register")
	public void register(Consumer<ResourcePackProfile> consumer, ResourcePackProfile.Factory factory, CallbackInfo ci) {
		BedrockResourcePackAdapter adapter = new BedrockResourcePackAdapter();
		consumer.accept(ResourcePackProfile.of(
				"BedrockAssets", true, () -> adapter,
				factory, ResourcePackProfile.InsertionPosition.TOP,
				ResourcePackSource.field_25347
		));
	}
}
