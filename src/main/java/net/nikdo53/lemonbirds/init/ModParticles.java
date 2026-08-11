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


}
