package net.nikdo53.lemonbirds.init;

import net.neoforged.neoforge.common.ModConfigSpec;

public interface ModServerConfig {
    ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    ModConfigSpec.DoubleValue SLINGSHOT_RANGE_MULTIPLIER = BUILDER
            .translation("lemonbirds.configuration.slingshot_range_multiplier")
            .defineInRange("slingshot_range_multiplier", 1.0d, 0.0d, 10.0d);

    ModConfigSpec SERVER_CONFIG = BUILDER.build();

}
