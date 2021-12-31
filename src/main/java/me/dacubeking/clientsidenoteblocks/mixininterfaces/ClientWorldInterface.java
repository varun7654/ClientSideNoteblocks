package me.dacubeking.clientsidenoteblocks.mixininterfaces;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;

public interface ClientWorldInterface {


    void bypassedPlaySound(PlayerEntity player, BlockPos pos, SoundEvent sound, SoundCategory voice, float v, float f);
}
