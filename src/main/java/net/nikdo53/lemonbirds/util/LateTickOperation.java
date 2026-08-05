package net.nikdo53.lemonbirds.util;

import net.minecraft.server.level.ServerLevel;
import net.nikdo53.lemonbirds.LemonBirds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * An operation that runs a set number of ticks in the future, on the level it was scheduled for.
 * <p>
 * The level is part of the operation because {@code LevelTickEvent} fires once per dimension. Without it the countdown
 * is decremented once per dimension instead of once per tick, and the operation ends up running against whichever
 * dimension happened to tick when the delay ran out - normally the end, since it ticks last.
 */
public class LateTickOperation {
    public static final List<LateTickOperation> SUB_LEVEL_OPERATIONS = new ArrayList<>();

    public final ServerLevel level;
    public int tick;
    public final Consumer<ServerLevel> operation;

    public LateTickOperation(ServerLevel level, int tick, Consumer<ServerLevel> operation) {
        this.level = level;
        this.tick = tick;
        this.operation = operation;
    }

    public static void schedule(ServerLevel level, int delay, Consumer<ServerLevel> operation) {
        SUB_LEVEL_OPERATIONS.add(new LateTickOperation(level, delay, operation));
    }

    /**
     * Runs every queued operation belonging to {@code level} whose delay has run out, and counts down the rest.
     */
    public static void run(ServerLevel level) {
        List<LateTickOperation> due = null;

        for (Iterator<LateTickOperation> iterator = SUB_LEVEL_OPERATIONS.iterator(); iterator.hasNext(); ) {
            LateTickOperation lateTickOperation = iterator.next();

            if (lateTickOperation.level != level) continue;

            if (lateTickOperation.tick <= 0) {
                iterator.remove();

                if (due == null) due = new ArrayList<>();
                due.add(lateTickOperation);
            } else {
                lateTickOperation.tick--;
            }
        }

        if (due == null) return;

        // Run outside the loop above, so operations that queue a follow up don't mutate the list while it's iterated
        for (LateTickOperation lateTickOperation : due) {
            try {
                lateTickOperation.operation.accept(level);
            } catch (Exception e) {
                LemonBirds.LOGGER.error("Late tick operation failed", e);
            }
        }
    }
}
