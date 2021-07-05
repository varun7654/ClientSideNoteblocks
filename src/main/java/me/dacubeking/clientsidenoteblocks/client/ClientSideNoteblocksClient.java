package me.dacubeking.clientsidenoteblocks.client;

import me.dacubeking.clientsidenoteblocks.expiringmap.NoteblockData;
import me.dacubeking.clientsidenoteblocks.expiringmap.SelfExpiringHashMap;
import me.dacubeking.clientsidenoteblocks.mixininterfaces.ClientWorldInterface;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.enums.Instrument;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.block.NoteBlock.INSTRUMENT;
import static net.minecraft.block.NoteBlock.NOTE;

@Environment(EnvType.CLIENT)
public class ClientSideNoteblocksClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if(world.isClient){
                if(world.getBlockState(pos).getBlock().getClass() == NoteBlock.class){
                    BlockState blockState = world.getBlockState(pos);

                    int i = blockState.get(NOTE);
                    float f = (float)Math.pow(2.0D, (double)(i - 12) / 12.0D);
                    if (world.getBlockState(pos.up()).isAir()) {
                        if(MinecraftClient.getInstance().world != null){

                            ClientWorldInterface clientWorldInterface =((ClientWorldInterface) MinecraftClient.getInstance().world);

                            clientWorldInterface.bypassedPlaySound(player, pos, blockState.get(INSTRUMENT).getSound(), SoundCategory.RECORDS, 3.0F, f);

                            SelfExpiringHashMap<BlockPos, Integer> cancelableNoteblockSounds = NoteblockData.cancelableNoteblockSounds;

                            if(cancelableNoteblockSounds.containsKey(pos)){
                                cancelableNoteblockSounds.put(pos, cancelableNoteblockSounds.get(pos) + 1);
                            } else {
                                cancelableNoteblockSounds.put(pos, 1);
                            }
                        }

                    }
                }

            }

            return ActionResult.PASS;

        });
    }
}
