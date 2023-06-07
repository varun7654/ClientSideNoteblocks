package me.dacubeking.clientsidenoteblocks.client;

import me.dacubeking.clientsidenoteblocks.expiringmap.NoteblockData;
import me.dacubeking.clientsidenoteblocks.expiringmap.SelfExpiringHashMap;
import me.dacubeking.clientsidenoteblocks.mixininterfaces.ClientWorldInterface;
import me.dacubeking.clientsidenoteblocks.mixininterfaces.NoteblockInterface;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.enums.Instrument;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.logging.Logger;

import static net.minecraft.block.NoteBlock.INSTRUMENT;
import static net.minecraft.block.NoteBlock.NOTE;

@Environment(EnvType.CLIENT)
public class ClientSideNoteblocksClient implements ClientModInitializer {

    public static final boolean debug = false;

    public static final Logger LOGGER = Logger.getLogger("ClientSideNoteblocks");

    @Override
    public void onInitializeClient() {
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (world.isClient && !player.isCreative() && !player.isSpectator()
                    && world.getBlockState(pos).getBlock().getClass() == NoteBlock.class) {
                BlockState state = world.getBlockState(pos);

                if (MinecraftClient.getInstance().world != null &&
                        (state.get(INSTRUMENT).isNotBaseBlock() || world.getBlockState(pos.up()).isAir())) {
                    ClientWorldInterface clientWorldInterface = ((ClientWorldInterface) MinecraftClient.getInstance().world);

                    RegistryEntry<SoundEvent> registryEntry;
                    float f;
                    Instrument instrument = state.get(INSTRUMENT);
                    if (instrument.shouldSpawnNoteParticles()) {
                        int i = state.get(NOTE);
                        f = NoteBlock.getNotePitch(i);
                    } else {
                        f = 1.0f;
                    }

                    if (instrument.hasCustomSound()) {
                        Identifier identifier = ((NoteblockInterface) state.getBlock()).getCustomSoundPublic(world, pos);
                        if (identifier == null) {
                            return ActionResult.PASS;
                        }
                        registryEntry = RegistryEntry.of(SoundEvent.of(identifier));
                    } else {
                        registryEntry = instrument.getSound();
                    }

                    clientWorldInterface.bypassedPlaySound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, registryEntry, SoundCategory.RECORDS, 3.0f, f, world.random.nextLong());

                    SelfExpiringHashMap<BlockPos, Integer> cancelableNoteblockSounds = NoteblockData.cancelableNoteblockSounds;

                    if (cancelableNoteblockSounds.containsKey(pos)) {
                        cancelableNoteblockSounds.put(pos, cancelableNoteblockSounds.get(pos) + 1);
                    } else {
                        cancelableNoteblockSounds.put(pos, 1);
                    }
                }


            }
            return ActionResult.PASS;
        });
    }
}
