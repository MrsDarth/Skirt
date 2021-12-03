package io.github.mrsdarth.skirt.elements.reflect.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;
import ch.njol.util.StringUtils;
import com.comphenix.protocol.utility.MinecraftFields;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.google.common.base.Defaults;
import de.tr7zw.changeme.nbtapi.NBTEntity;
import io.github.mrsdarth.skirt.Reflectness;
import io.github.mrsdarth.skirt.Skirtness;
import io.github.mrsdarth.skirt.protocolLib.PLib;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.ArrayUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

@Name("Move entity")
@Description({"Similar to teleporting but keeps their momentum and works even if there are passengers on the entity",
        "the location must be in the same world as the entity"})
@Examples("move player 10 above player")
@Since("1.1.0")

@SuppressWarnings("unchecked")
public class EffMoveEntity extends Effect {

    private static Method TELEPORT_PLAYER, TELEPORT_ENTITY;
    private static Set<?> FLAGS;

    private static boolean findPlayerMethod() {
        if (Skirtness.hasProtocolLib())
            for (Method method : MinecraftReflection.getPlayerConnectionClass().getDeclaredMethods())
                if (StringUtils.contains(method.getName(), "internalTeleport", false)) {
                    TELEPORT_PLAYER = method;
                    for (Type type : method.getGenericParameterTypes())
                        if (type instanceof ParameterizedType flagSetParam && flagSetParam.getActualTypeArguments()[0] instanceof Class flagsClass) {
                            FLAGS = EnumSet.allOf(flagsClass);
                            return true;
                        }
                }
        return false;
    }

    private static boolean findEntityMethod() {
        if (Skirtness.hasNBT()) return true;
        if (Skirtness.hasProtocolLib()) {
            TELEPORT_ENTITY = Reflectness.getMethod(MinecraftReflection.getEntityClass(), "setLocation", double.class, double.class, double.class, float.class, float.class);
            return TELEPORT_ENTITY != null;
        }
        return false;
    }

    static {
        if (findPlayerMethod() || findEntityMethod())
            Skript.registerEffect(EffMoveEntity.class,
                    "(move|set location of) %entities% (to|%directions%) [%-location%]");
    }

    public static void move(Entity entity, Location loc) {
        move(entity, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }

    private static void move(Entity entity, double x, double y, double z, float yaw, float pitch) {
        if (entity instanceof Player player) {
            if (TELEPORT_PLAYER == null) return;
            Object[] params = Arrays.stream(TELEPORT_PLAYER.getParameterTypes()).map(Defaults::defaultValue).toArray();
            Reflectness.invoke(TELEPORT_PLAYER, MinecraftFields.getPlayerConnection(player), ArrayUtils.insert(0, params, x, y, z, yaw, pitch, FLAGS));
        } else if (TELEPORT_ENTITY != null) {
            Reflectness.invoke(TELEPORT_ENTITY, PLib.toNMS(entity), x, y, z, yaw, pitch);
        } else if (Skirtness.hasNBT()) {
            entity.setRotation(yaw, pitch);
            Collections.addAll(new NBTEntity(entity).getDoubleList("Pos"), x, y, z);
        }
    }




    private Expression<Entity> entityExpr;
    private Expression<Direction> directionExpr;
    private Expression<Location> locationExpr;

    @Override
    protected void execute(@NotNull Event e) {
        if (locationExpr == null) {
            Direction[] directions = directionExpr.getArray(e);
            for (Entity entity: entityExpr.getArray(e)) {
                Location dest = entity.getLocation();
                for (Direction direction: directions)
                    dest.add(direction.getDirection(entity));
                move(entity, dest);
            }
        } else
            Skirtness.getSingle(locationExpr, e).ifPresent(location -> {
                for (Entity entity : entityExpr.getArray(e))
                    move(entity, location);
            });
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "move " + entityExpr.toString(e, debug) + " " + (directionExpr == null ? "to" : directionExpr.toString(e, debug)) + (locationExpr == null ? "" : " " + locationExpr.toString(e, debug));
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int i, @NotNull Kleenean kleenean, @NotNull ParseResult parseResult) {
        entityExpr = (Expression<Entity>) exprs[0];
        directionExpr = (Expression<Direction>) exprs[1];
        locationExpr = (Expression<Location>) exprs[2];
        boolean hasLocation = locationExpr != null;
        if (hasLocation)
            locationExpr = Direction.combine(directionExpr, locationExpr);
        return (hasLocation || directionExpr != null);
    }
}
