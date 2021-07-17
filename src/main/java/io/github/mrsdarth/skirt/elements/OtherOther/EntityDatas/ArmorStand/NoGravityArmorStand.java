package io.github.mrsdarth.skirt.elements.OtherOther.EntityDatas.ArmorStand;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.event.CraftEventFactory;


public class NoGravityArmorStand extends EntityArmorStand {

    public NoGravityArmorStand(World world, Location loc) {
        super(world,loc.getX(),loc.getY(),loc.getZ());
        this.setYawPitch(loc.getYaw(), loc.getPitch());
        this.setNoGravity(true);
    }

    // nms living entity methods

    @Override
    public boolean doAITick() {
        return !this.world.isClientSide;
    }

    @Override
    public void g(Vec3D vec3d) {
        if (doAITick() || cs()) {
            double d0 = 0.08D;
            boolean flag = ((getMot()).y <= 0.0D);
            if (flag && hasEffect(MobEffects.SLOW_FALLING)) {
                d0 = 0.01D;
                this.fallDistance = 0.0F;
            }
            Fluid fluid = this.world.getFluid(getChunkCoordinates());
            if (isInWater() && cT() && !a(fluid.getType())) {
                double d1 = locY();
                float f = isSprinting() ? 0.9F : dM();
                float f1 = 0.02F;
                float f2 = EnchantmentManager.e(this);
                if (f2 > 3.0F)
                    f2 = 3.0F;
                if (!this.onGround)
                    f2 *= 0.5F;
                if (f2 > 0.0F) {
                    f += (0.54600006F - f) * f2 / 3.0F;
                    f1 += (dN() - f1) * f2 / 3.0F;
                }
                if (hasEffect(MobEffects.DOLPHINS_GRACE))
                    f = 0.96F;
                a(f1, vec3d);
                move(EnumMoveType.SELF, getMot());
                Vec3D vec3d1 = getMot();
                if (this.positionChanged && isClimbing())
                    vec3d1 = new Vec3D(vec3d1.x, 0.2D, vec3d1.z);
                setMot(vec3d1.d(f, 0.800000011920929D, f));
                Vec3D vec3d2 = a(d0, flag, getMot());
                setMot(vec3d2);
                if (this.positionChanged && e(vec3d2.x, vec3d2.y + 0.6000000238418579D - locY() + d1, vec3d2.z))
                    setMot(vec3d2.x, 0.30000001192092896D, vec3d2.z);
            } else if (aQ() && cT() && !a(fluid.getType())) {
                double d1 = locY();
                a(0.02F, vec3d);
                move(EnumMoveType.SELF, getMot());
                if (b(TagsFluid.LAVA) <= cx()) {
                    setMot(getMot().d(0.5D, 0.800000011920929D, 0.5D));
                    Vec3D vec3D = a(d0, flag, getMot());
                    setMot(vec3D);
                } else {
                    setMot(getMot().a(0.5D));
                }
                if (!isNoGravity())
                    setMot(getMot().add(0.0D, -d0 / 4.0D, 0.0D));
                Vec3D vec3d3 = getMot();
                if (this.positionChanged && e(vec3d3.x, vec3d3.y + 0.6000000238418579D - locY() + d1, vec3d3.z))
                    setMot(vec3d3.x, 0.30000001192092896D, vec3d3.z);
            } else if (isGliding()) {
                Vec3D vec3d4 = getMot();
                if (vec3d4.y > -0.5D)
                    this.fallDistance = 1.0F;
                Vec3D vec3d5 = getLookDirection();
                float f = this.pitch * 0.017453292F;
                double d2 = Math.sqrt(vec3d5.x * vec3d5.x + vec3d5.z * vec3d5.z);
                double d3 = Math.sqrt(c(vec3d4));
                double d4 = vec3d5.f();
                float f3 = MathHelper.cos(f);
                f3 = (float)(f3 * f3 * Math.min(1.0D, d4 / 0.4D));
                vec3d4 = getMot().add(0.0D, d0 * (-1.0D + f3 * 0.75D), 0.0D);
                if (vec3d4.y < 0.0D && d2 > 0.0D) {
                    double d5 = vec3d4.y * -0.1D * f3;
                    vec3d4 = vec3d4.add(vec3d5.x * d5 / d2, d5, vec3d5.z * d5 / d2);
                }
                if (f < 0.0F && d2 > 0.0D) {
                    double d5 = d3 * -MathHelper.sin(f) * 0.04D;
                    vec3d4 = vec3d4.add(-vec3d5.x * d5 / d2, d5 * 3.2D, -vec3d5.z * d5 / d2);
                }
                if (d2 > 0.0D)
                    vec3d4 = vec3d4.add((vec3d5.x / d2 * d3 - vec3d4.x) * 0.1D, 0.0D, (vec3d5.z / d2 * d3 - vec3d4.z) * 0.1D);
                setMot(vec3d4.d(0.9900000095367432D, 0.9800000190734863D, 0.9900000095367432D));
                move(EnumMoveType.SELF, getMot());
                if (this.positionChanged && !this.world.isClientSide) {
                    double d5 = Math.sqrt(c(getMot()));
                    double d6 = d3 - d5;
                    float f4 = (float)(d6 * 10.0D - 3.0D);
                    if (f4 > 0.0F) {
                        playSound(getSoundFall((int)f4), 1.0F, 1.0F);
                        damageEntity(DamageSource.FLY_INTO_WALL, f4);
                    }
                }
                if (this.onGround && !this.world.isClientSide && getFlag(7) && !CraftEventFactory.callToggleGlideEvent(this, false).isCancelled())
                    setFlag(7, false);
            } else {
                BlockPosition blockposition = as();
                float f5 = this.world.getType(blockposition).getBlock().getFrictionFactor();
                float f = this.onGround ? (f5 * 0.91F) : 0.91F;
                Vec3D vec3d6 = a(vec3d, f5);
                double d7 = vec3d6.y;
                if (hasEffect(MobEffects.LEVITATION)) {
                    d7 += (0.05D * (getEffect(MobEffects.LEVITATION).getAmplifier() + 1) - vec3d6.y) * 0.2D;
                    this.fallDistance = 0.0F;
                } else if (this.world.isClientSide && !this.world.isLoaded(blockposition)) {
                    if (locY() > 0.0D) {
                        d7 = -0.1D;
                    } else {
                        d7 = 0.0D;
                    }
                } else if (!isNoGravity()) {
                    d7 -= d0;
                }
                setMot(vec3d6.x * f, d7 * 0.9800000190734863D, vec3d6.z * f);
            }
        }
        a(this, this instanceof EntityBird);
    }
}
