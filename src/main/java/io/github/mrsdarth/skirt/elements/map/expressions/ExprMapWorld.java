package io.github.mrsdarth.skirt.elements.map.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Map World")
@Description("the world of a map")
@Examples("set world of map of player's tool to world of player")
@Since("1.2.0")

public class ExprMapWorld extends SimplePropertyExpression<MapView, World> {

    static {
        register(ExprMapWorld.class, World.class, "map world", "maps");
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "map world";
    }

    @Nullable
    @Override
    public World convert(MapView mapView) {
        return mapView.getWorld();
    }

    @Override
    public @NotNull Class<? extends World> getReturnType() {
        return World.class;
    }

    @Override
    public Class<?>[] acceptChange(Changer.@NotNull ChangeMode mode) {
        return mode == Changer.ChangeMode.SET ? CollectionUtils.array(World.class) : null;
    }

    @Override
    public void change(@NotNull Event e, Object @Nullable [] delta, Changer.@NotNull ChangeMode mode) {
        if (delta != null && delta.length != 0 && delta[0] instanceof World world)
            for (MapView map : getExpr().getArray(e))
                map.setWorld(world);
    }
}
