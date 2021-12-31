package me.dacubeking.clientsidenoteblocks.expiringmap;

import net.minecraft.util.math.BlockPos;

public class NoteblockData {
    public static SelfExpiringHashMap<BlockPos, Integer> cancelableNoteblockSounds = new SelfExpiringHashMap<>(50000, 100);
}
