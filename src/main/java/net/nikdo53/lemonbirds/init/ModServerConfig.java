package net.nikdo53.lemonbirds.init;

import net.neoforged.neoforge.common.ModConfigSpec;

public interface ModServerConfig {
    ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    ModConfigSpec.DoubleValue SLINGSHOT_RANGE_MULTIPLIER = BUILDER
            .translation("lemonbirds.configuration.slingshot_range_multiplier")
            .defineInRange("slingshot_range_multiplier", 1.0d, 0.0d, 10.0d);

    ModConfigSpec.BooleanValue SHOOT_FROM_HAND = BUILDER
            .translation("lemonbirds.configuration.shoot_from_hand")
            .define("shoot_from_hand", true);

    ModConfigSpec SERVER_CONFIG = BUILDER.build();

}
