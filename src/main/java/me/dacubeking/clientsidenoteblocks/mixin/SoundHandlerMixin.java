package me.dacubeking.clientsidenoteblocks.mixin;

import me.dacubeking.clientsidenoteblocks.expiringmap.NoteblockData;
import me.dacubeking.clientsidenoteblocks.mixininterfaces.SoundHandlerInterface;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundManager.class)
public class SoundHandlerMixin implements SoundHandlerInterface {

    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"), cancellable = true)
    public void onPlay(@NotNull SoundInstance sound, CallbackInfo ci) {
        BlockPos pos = new BlockPos(sound.getX() - 0.5, sound.getY() - 0.5, sound.getZ() - 0.5);

        if (NoteblockData.cancelableNoteblockSounds.containsKey(pos)) {
            int amount = NoteblockData.cancelableNoteblockSounds.get(pos);
            if (amount < 2) {
                NoteblockData.cancelableNoteblockSounds.remove(pos);
            } else {
                NoteblockData.cancelableNoteblockSounds.put(pos, amount - 1);
            }
            ci.cancel();

        }
    }


    @Final
    @Shadow private SoundSystem soundSystem;

    @Override
    public void bypassedPlay(SoundInstance sound) {
        this.soundSystem.play(sound);
    }
}
