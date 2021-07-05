package me.dacubeking.clientsidenoteblocks.mixin;

import me.dacubeking.clientsidenoteblocks.expiringmap.NoteblockData;
import me.dacubeking.clientsidenoteblocks.expiringmap.SelfExpiringHashMap;
import me.dacubeking.clientsidenoteblocks.mixininterfaces.ClientWorldInterface;
import me.dacubeking.clientsidenoteblocks.mixininterfaces.SoundHandlerInterface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ClientWorld.class)
public class ClientWorldMixin implements ClientWorldInterface {

    @Final
    @Shadow
    private MinecraftClient client;

    public void bypassedPlaySound(@Nullable PlayerEntity player, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        if (Objects.equals(player, this.client.player)) {
            PositionedSoundInstance positionedSoundInstance = new PositionedSoundInstance(sound, category, volume, pitch, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5);
            SoundHandlerInterface soundManager = (SoundHandlerInterface) this.client.getSoundManager();
            soundManager.bypassedPlay(positionedSoundInstance);
        }
    }

}
