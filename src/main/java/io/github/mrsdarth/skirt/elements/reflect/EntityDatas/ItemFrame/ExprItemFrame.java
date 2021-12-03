package io.github.mrsdarth.skirt.elements.reflect.EntityDatas.ItemFrame;

import ch.njol.skript.Skript;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.entity.SimpleEntityData;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import io.github.mrsdarth.skirt.Reflectness;
import io.github.mrsdarth.skirt.Skirtness;
import io.github.mrsdarth.skirt.elements.direction.DirectionUtils;
import io.github.mrsdarth.skirt.protocolLib.PLib;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

@SuppressWarnings("unchecked")
public class ExprItemFrame extends SimpleExpression<EntityData> {

    private static Constructor<?> getEntityItemFrame, getGlowItemFrame;

    private static Method addEntity;

    static {
        if (Skirtness.hasProtocolLib()) {
            getEntityItemFrame = Reflectness.getConstructor(MinecraftReflection.getMinecraftClass("world.entity.decoration.EntityItemFrame", "EntityItemFrame"), MinecraftReflection.getNmsWorldClass(), MinecraftReflection.getBlockPositionClass(), EnumWrappers.getDirectionClass());
            getGlowItemFrame = Reflectness.getConstructor(MinecraftReflection.getMinecraftClass("world.entity.decoration.GlowItemFrame", "GlowItemFrame"), MinecraftReflection.getNmsWorldClass(), MinecraftReflection.getBlockPositionClass(), EnumWrappers.getDirectionClass());
            addEntity = Reflectness.getMethod(MinecraftReflection.getWorldServerClass(), "addEntity", MinecraftReflection.getEntityClass());
            Skript.registerExpression(ExprItemFrame.class, EntityData.class, ExpressionType.SIMPLE,
                    "[(1¦invisible)] (forced|fixed) [(2¦glow[ing])] item frame [facing %-direction%]");
        }
    }

    private Expression<Direction> directionExpr;
    private int mark;

    @Override
    protected EntityData<?> @NotNull [] get(@NotNull Event e) {
        Direction direction = directionExpr == null ? null : directionExpr.getSingle(e);
        return CollectionUtils.array(new SimpleEntityData() {
            @Override
            public @Nullable Entity spawn(@NotNull Location loc) {
                Location spawnLoc = direction == null ? loc : loc.clone().setDirection(direction.getDirection(loc));
                World world = spawnLoc.getWorld();
                Constructor<?> constructor = (mark & 2) == 0 ? getEntityItemFrame : getGlowItemFrame;
                Object nmsWorld = PLib.toNMS(world);
                Object nmsItemFrame = Reflectness.newInstance(constructor, nmsWorld, BlockPosition.getConverter().getGeneric(new BlockPosition(loc.toVector())), EnumWrappers.getDirectionConverter().getGeneric(EnumWrappers.Direction.valueOf(DirectionUtils.nearestBlockFaceCartesian(loc.getDirection()).name())));
                ItemFrame itemFrame = (ItemFrame) MinecraftReflection.getBukkitEntity(nmsItemFrame);
                if (itemFrame == null) return null;
                itemFrame.setFixed(true);
                itemFrame.setVisible((mark & 1) == 0);
                Reflectness.invoke(addEntity, world, nmsItemFrame);
                return itemFrame.isValid() ? itemFrame : null;
            }
        });
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends EntityData> getReturnType() {
        return EntityData.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return ((mark & 1) == 0 ? "" : "invisible ") + "forced " + ((mark & 2) == 0 ? "" : "glowing ") + "item frame" + (directionExpr == null ? "" : " facing " + directionExpr.toString(e, debug));
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        directionExpr = (Expression<Direction>) exprs[0];
        mark = parseResult.mark;
        return true;
    }
}
