package net.nikdo53.lemonbirds.init;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nikdo53.lemonbirds.LemonBirds;
import net.nikdo53.lemonbirds.particle.BirdTrailParticle;

import java.util.function.Supplier;

public interface ModParticles {
    DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, LemonBirds.MOD_ID);

    Supplier<SimpleParticleType> LEMON_BIRD_TRAIL = PARTICLES.register("lemon_bird_trail", () -> new SimpleParticleType(false));
    Supplier<SimpleParticleType> LEMON_BIRD_TRAIL_PREVIEW = PARTICLES.register("lemon_bird_trail_preview", () -> new SimpleParticleType(false));
    Supplier<SimpleParticleType> LEMON_BIRD_ABILITY = PARTICLES.register("lemon_bird_ability", () -> new SimpleParticleType(false));

    Supplier<SimpleParticleType> FEATHER_RED = PARTICLES.register("feather_red", () -> new SimpleParticleType(false));
    Supplier<SimpleParticleType> FEATHER_BLUE = PARTICLES.register("feather_blue", () -> new SimpleParticleType(false));
    Supplier<SimpleParticleType> FEATHER_YELLOW = PARTICLES.register("feather_yellow", () -> new SimpleParticleType(false));
    Supplier<SimpleParticleType> FEATHER_BOMB = PARTICLES.register("feather_bomb", () -> new SimpleParticleType(false));
    Supplier<SimpleParticleType> FEATHER_MATILDA = PARTICLES.register("feather_matilda", () -> new SimpleParticleType(false));
    Supplier<SimpleParticleType> FEATHER_TERENCE = PARTICLES.register("feather_terence", () -> new SimpleParticleType(false));

    Supplier<SimpleParticleType> PIG_BLOB = PARTICLES.register("pig_blob", () -> new SimpleParticleType(false));


}
