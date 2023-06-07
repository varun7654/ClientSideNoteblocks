package me.dacubeking.clientsidenoteblocks.mixin;

import me.dacubeking.clientsidenoteblocks.client.ClientSideNoteblocksClient;
import me.dacubeking.clientsidenoteblocks.expiringmap.NoteblockData;
import me.dacubeking.clientsidenoteblocks.mixininterfaces.ClientWorldInterface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin extends World implements ClientWorldInterface {

    @Final
    @Shadow
    private MinecraftClient client;

    // Ignored by Mixin
    protected ClientWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    @Inject(method = "playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/sound/SoundCategory;FFJ)V", at = @At("HEAD"), cancellable = true)
    public void playSound(PlayerEntity except, double x, double y, double z, RegistryEntry<SoundEvent> sound, SoundCategory category, float volume, float pitch, long seed, CallbackInfo ci) {
        BlockPos pos = new BlockPos((int) (x - 0.5), (int) (y - 0.5), (int) (z - 0.5));

        if (NoteblockData.cancelableNoteblockSounds.containsKey(pos)) {
            int amount = NoteblockData.cancelableNoteblockSounds.get(pos);
            if (amount < 2) {
                NoteblockData.cancelableNoteblockSounds.remove(pos);
            } else {
                NoteblockData.cancelableNoteblockSounds.put(pos, amount - 1);
            }
            if (ClientSideNoteblocksClient.debug) {
                ClientSideNoteblocksClient.LOGGER.info("Cancelled server note block sound");
            }
            ci.cancel();
        }
    }


    @Override
    public void bypassedPlaySound(@Nullable PlayerEntity except, double x, double y, double z, RegistryEntry<SoundEvent> sound, SoundCategory category, float volume, float pitch, long seed) {
        if (ClientSideNoteblocksClient.debug) {
            ClientSideNoteblocksClient.LOGGER.info("Bypassed played sound");
        }
 
        this.playSound(x, y, z, (SoundEvent) sound.value(), category, volume, pitch, false, seed);

    }

    @Shadow
    private void playSound(double x, double y, double z, SoundEvent value, SoundCategory category, float volume, float pitch, boolean b, long seed) {
    }

}
