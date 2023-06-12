package me.dacubeking.clientsidenoteblocks.client;

import me.dacubeking.clientsidenoteblocks.mixininterfaces.ClientWorldInterface;
import me.dacubeking.clientsidenoteblocks.mixininterfaces.NoteblockInterface;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.enums.Instrument;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import static me.dacubeking.clientsidenoteblocks.expiringmap.NoteblockData.NOTEBLOCK_SOUNDS_TO_CANCEL;
import static net.minecraft.block.NoteBlock.INSTRUMENT;
import static net.minecraft.block.NoteBlock.NOTE;

@Environment(EnvType.CLIENT)
public class ClientSideNoteblocksClient implements ClientModInitializer {

    public static ModConfig config;

    public static final Logger LOGGER = Logger.getLogger("ClientSideNoteblocks");

    public static boolean isDebug() {
        return config.debug;
    }

    public static boolean isEnabled() {
        return config.enabled;
    }

    @Override
    public void onInitializeClient() {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();


        KeyBinding toggleKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("text.clientsidenoteblocks.keybind.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_BRACKET, "text.clientsidenoteblocks.keybind.category"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKeybind.wasPressed()) {
                if (client.player == null) return;

                config.enabled = !config.enabled;
                if (config.enabled) {
                    client.player.sendMessage(Text.translatableWithFallback("text.clientsidenoteblocks.chat.enabled",
                            "ClientSideNoteblocks is Enabled"), false);

                } else {
                    client.player.sendMessage(Text.translatableWithFallback("text.clientsidenoteblocks.chat.disabled",
                            "ClientSideNoteblocks is Disabled"), false);
                }
            }
        });

        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (!isEnabled()) return ActionResult.PASS;
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


                    synchronized (NOTEBLOCK_SOUNDS_TO_CANCEL) {
                        if (NOTEBLOCK_SOUNDS_TO_CANCEL.containsKey(pos)) {
                            NOTEBLOCK_SOUNDS_TO_CANCEL.get(pos).addAndGet(2);
                        } else {
                            NOTEBLOCK_SOUNDS_TO_CANCEL.put(pos, new AtomicInteger(2));
                        }
                    }
                }


            }
            return ActionResult.PASS;
        });
    }
}
