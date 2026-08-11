package net.nikdo53.lemonbirds.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class BirdTrailParticle extends TextureSheetParticle implements ParticleOptions {
    final SimpleParticleType type;
    SpriteSet sprites;

    public BirdTrailParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SimpleParticleType type, SpriteSet sprites, int lifetime, int size) {
        super(level, x, y, z);
        this.type = type;

        setParticleSpeed(xSpeed, ySpeed, zSpeed);
        setLifetime(lifetime);
        setColor(1, 1, 1);
        scale(size);
        this.sprites = sprites;
        this.setSpriteFromAge(sprites);
    }

    @Override
    public ParticleType<?> getType() {
        return type;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
    }


    public static class Provider implements ParticleProvider<SimpleParticleType>{
        private final SpriteSet sprites;
        private final int lifetime;
        private final int size;

        public Provider(SpriteSet sprites, int lifetime, int size) {
            this.sprites = sprites;
            this.lifetime = lifetime;
            this.size = size;
        }

        public static Provider createLiving(SpriteSet sprites) {
            return new Provider(sprites, 100, 2);
        }

        public static Provider createAbility(SpriteSet sprites) {
            return new Provider(sprites, 100, 4);
        }


        public static Provider createPreview(SpriteSet sprites) {
            return new Provider(sprites, 20, 2);
        }


        @Override
        public @Nullable Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new BirdTrailParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, type, sprites, lifetime, size);
        }
    }
}
