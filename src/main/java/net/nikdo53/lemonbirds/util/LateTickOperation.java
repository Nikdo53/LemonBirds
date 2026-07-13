package net.nikdo53.lemonbirds.util;

import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LateTickOperation {
    public static final List<LateTickOperation> SUB_LEVEL_OPERATIONS = new ArrayList<>();

    public int tick;
    public final Consumer<ServerLevel> operation;

    public LateTickOperation(int tick, Consumer<ServerLevel> operation) {
        this.tick = tick;
        this.operation = operation;
    }
}
