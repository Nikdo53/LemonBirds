package net.nikdo53.lemonbirds.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class BirdTrailParticle extends DustParticle implements ParticleOptions {
    final SimpleParticleType type;

    public BirdTrailParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SimpleParticleType type, SpriteSet sprites, int lifetime) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, new DustParticleOptions(new Vector3f(1), 1), sprites);
        this.type = type;

        setLifetime(lifetime);
        setColor(1, 1, 1);
    }

    @Override
    public ParticleType<?> getType() {
        return type;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType>{
        private final SpriteSet sprites;
        private final int lifetime;

        public Provider(SpriteSet sprites, int lifetime) {
            this.sprites = sprites;
            this.lifetime = lifetime;
        }

        public static Provider createLiving(SpriteSet sprites) {
            return new Provider(sprites, 100);
        }

        public static Provider createPreview(SpriteSet sprites) {
            return new Provider(sprites, 20);
        }


        @Override
        public @Nullable Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new BirdTrailParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, type, sprites, lifetime);
        }
    }
}
