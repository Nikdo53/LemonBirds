package net.nikdo53.lemonbirds.init;

import net.neoforged.neoforge.common.ModConfigSpec;

public interface ModClientConfig {
    ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    ModConfigSpec.BooleanValue SHOW_PIG_HIGHLIGHTS = BUILDER
            .translation("lemonbirds.configuration.show_pig_outlines")
            .define("show_pig_outlines", true);

    ModConfigSpec CLIENT = BUILDER.build();

}
