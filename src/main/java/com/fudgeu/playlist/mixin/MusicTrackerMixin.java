package com.fudgeu.playlist.mixin;

import net.minecraft.client.sounds.MusicManager;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({Minecraft.class})
public class MusicTrackerMixin {
    @Redirect(
        method = {"tick"},
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/sounds/MusicManager;tick()V"
        )
    )
    private void playlistTick(MusicManager instance) {
    }
}
