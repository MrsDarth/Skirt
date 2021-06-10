package io.github.mrsdarth.skirt.elements.Map.expressions;

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
import org.jetbrains.annotations.Nullable;

@Name("Map World")
@Description("the world of a map")
@Examples("set world of map of player's tool to world of player")
@Since("1.2.0")

public class ExprMapWorld extends SimplePropertyExpression<MapView, World> {

    static {
        register(ExprMapWorld.class, World.class, "[map] world", "maps");
    }

    @Override
    protected String getPropertyName() {
        return "map world";
    }

    @Nullable
    @Override
    public World convert(MapView mapView) {
        return mapView.getWorld();
    }

    @Override
    public Class<? extends World> getReturnType() {
        return World.class;
    }

    @Nullable
    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        return mode == Changer.ChangeMode.SET ? CollectionUtils.array(World.class) : null;
    }

    @Override
    public void change(Event e, @Nullable Object[] delta, Changer.ChangeMode mode) {
        World w = (World) delta[0];
        if (w == null) return;
        for (MapView map: getExpr().getArray(e)) {
            map.setWorld(w);
        }
    }
}
