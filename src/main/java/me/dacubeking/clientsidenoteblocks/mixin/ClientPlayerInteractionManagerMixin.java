package me.dacubeking.clientsidenoteblocks.mixin;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {

    @Final
    @Shadow
    private MinecraftClient client;

    @Shadow @Final private ClientPlayNetworkHandler networkHandler;

    @Redirect(method = "updateBlockBreakingProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/SoundManager;play(Lnet/minecraft/client/sound/SoundInstance;)V"))
    public void cancelBlockBreakSound(SoundManager soundManager, SoundInstance sound, BlockPos pos, Direction direction) {
        if (this.client.world == null || this.client.player == null ||
                this.client.player.isCreative() || this.client.player.isSpectator() ||
                !(this.client.world.getBlockState(pos).getBlock() == Blocks.NOTE_BLOCK)
                || !this.client.world.getBlockState(pos.up()).isAir()) {
            soundManager.play(sound);
        }
    }
}
