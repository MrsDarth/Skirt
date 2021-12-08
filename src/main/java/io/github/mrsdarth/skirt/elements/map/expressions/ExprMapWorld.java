package io.github.mrsdarth.skirt.elements.map.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@Name("Map World / Location")
@Description("the world / location of a map")
@Examples("set map world of map of player's tool to world of player")
@Since("1.2.0")

public class ExprMapWorld extends SimplePropertyExpression<MapView, Object> {

    static {
        register(ExprMapWorld.class, Object.class, "map (world|1¦loc[ation])", "maps");
    }

    boolean location;

    @Override
    protected @NotNull String getPropertyName() {
        return "map " + (location ? "location" : "world");
    }

    @Nullable
    @Override
    public Object convert(MapView mapView) {
        return location ? new Location(mapView.getWorld(), mapView.getCenterX(), 0, mapView.getCenterZ()) : mapView.getWorld();
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return location ? Location.class : World.class;
    }

    @Override
    public Class<?>[] acceptChange(Changer.@NotNull ChangeMode mode) {
        return mode == Changer.ChangeMode.SET ? CollectionUtils.array(getReturnType()) : null;
    }

    @Override
    public void change(@NotNull Event e, Object @Nullable [] delta, Changer.@NotNull ChangeMode mode) {
        if (ArrayUtils.isEmpty(delta)) return;
        Consumer<MapView> forEach;
        if (delta[0] instanceof World world)
            forEach = map -> map.setWorld(world);
        else if (delta[0] instanceof Location location)
            forEach = map -> {
                map.setCenterX(location.getBlockX());
                map.setCenterZ(location.getBlockZ());
            };
        else return;
        for (MapView mapView: getExpr().getArray(e))
            forEach.accept(mapView);
    }
}
