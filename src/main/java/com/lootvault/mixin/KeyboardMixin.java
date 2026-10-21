package com.lootvault.mixin;

import com.lootvault.LootVaultClient;
import net.minecraft.client.Keyboard;
import net.minecraft.client.input.KeyInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void onKeyInput(long window, int action, KeyInput input, CallbackInfo ci) {
        if (action == 1) {
            if (LootVaultClient.handleKeyInput(input.key(), input.scancode())) {
                ci.cancel();
            }
        }
    }
}
