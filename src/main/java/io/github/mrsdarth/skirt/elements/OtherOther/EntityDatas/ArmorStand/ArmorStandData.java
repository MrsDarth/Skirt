package io.github.mrsdarth.skirt.elements.OtherOther.EntityDatas.ArmorStand;

import ch.njol.skript.Skript;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import net.minecraft.server.v1_16_R3.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.Nullable;

public class ArmorStandData extends EntityData<ArmorStand> {

    static {
        if (Skript.classExists("net.minecraft.server.v1_16_R3.EntityArmorStand")) {
            EntityData.register(ArmorStandData.class, "skirt armor stand", ArmorStand.class, "skirt armor stand");
        }
    }


    @Override
    protected boolean init(Literal<?>[] literals, int i, SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected boolean init(@Nullable Class<? extends ArmorStand> aClass, @Nullable ArmorStand armorStand) {
        return true;
    }

    @Override
    public void set(ArmorStand armorStand) {
    }

    @Override
    protected boolean match(ArmorStand armorStand) {
        return ((CraftArmorStand) armorStand).getHandle() instanceof NoGravityArmorStand;
    }

    @Override
    public Class<? extends ArmorStand> getType() {
        return ArmorStand.class;
    }

    @Override
    public EntityData getSuperType() {
        return new ArmorStandData();
    }

    @Override
    protected int hashCode_i() {
        return 0;
    }

    @Override
    protected boolean equals_i(EntityData<?> entityData) {
        return entityData instanceof ArmorStandData;
    }

    @Override
    public boolean isSupertypeOf(EntityData<?> entityData) {
        return equals_i(entityData);
    }

    @Override
    public String toString(int flags) {
        return "armor stand";
    }

    @Nullable
    @Override
    public ArmorStand spawn(Location loc) {
        World nmsWorld = ((CraftWorld) loc.getWorld()).getHandle();
        NoGravityArmorStand noGravityArmorStand = new NoGravityArmorStand(nmsWorld, loc);
        nmsWorld.addEntity(noGravityArmorStand);
        return (ArmorStand) noGravityArmorStand.getBukkitEntity();
    }
}