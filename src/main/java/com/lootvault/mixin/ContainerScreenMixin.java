package com.lootvault.mixin;

import com.lootvault.core.LootEngine;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class ContainerScreenMixin {

    @Inject(method = "init", at = @At("TAIL"))
    private void onScreenInit(CallbackInfo ci) {
        LootEngine.checkAndLoot();
    }

    @Inject(method = "handledScreenTick", at = @At("HEAD"))
    private void onHandledScreenTick(CallbackInfo ci) {
        LootEngine.checkAndLoot();
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        LootEngine.checkAndLoot();
    }
}
