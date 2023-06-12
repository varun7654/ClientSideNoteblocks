package me.dacubeking.clientsidenoteblocks.expiringmap;

import net.minecraft.util.math.BlockPos;

import java.util.concurrent.atomic.AtomicInteger;

public class NoteblockData {
    private NoteblockData() {
    }

    public static final SelfExpiringHashMap<BlockPos, AtomicInteger> NOTEBLOCK_SOUNDS_TO_CANCEL = new SelfExpiringHashMap<>(50000, 100);
}
