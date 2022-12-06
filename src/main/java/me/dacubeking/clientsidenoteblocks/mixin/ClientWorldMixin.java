package me.dacubeking.clientsidenoteblocks.mixin;

import me.dacubeking.clientsidenoteblocks.mixininterfaces.ClientWorldInterface;
import me.dacubeking.clientsidenoteblocks.mixininterfaces.SoundHandlerInterface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Objects;
import java.util.function.Supplier;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin extends World implements ClientWorldInterface {

    @Final
    @Shadow
    private MinecraftClient client;

    //Ignored by mixin
    protected ClientWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, RegistryEntry<DimensionType> dimension, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, dimension, profiler, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }

    @Override
    public void bypassedPlaySound(@Nullable PlayerEntity player, BlockPos pos, RegistryEntry<SoundEvent> sound, SoundCategory category, float volume, float pitch) {
        if (Objects.equals(player, this.client.player)) {
            PositionedSoundInstance positionedSoundInstance = new PositionedSoundInstance(sound.value(), category, volume, pitch, Random.create(this.random.nextLong()), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            SoundHandlerInterface soundManager = (SoundHandlerInterface) this.client.getSoundManager();
            soundManager.bypassedPlay(positionedSoundInstance);
        }
    }

}
