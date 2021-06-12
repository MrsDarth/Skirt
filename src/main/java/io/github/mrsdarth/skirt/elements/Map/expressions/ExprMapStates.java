package io.github.mrsdarth.skirt.elements.Map.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.Nullable;

@Name("Map States")
@Description({
        "returns certain map states",
        "locked: whether the map can explore further",
        "position tracking: whether a position cursor should be shown when the map is near its center",
        "unlimited tracking: whether the map will show a smaller position cursor when cursor is outside of map's range"
})
@Examples("set position tracking state of {_map} to true")
@Since("1.2.0")

public class ExprMapStates extends SimplePropertyExpression<MapView, Boolean> {

    static {
        register(ExprMapStates.class, Boolean.class,
                "[map] (1¦lock[ed]|2¦position tracking|3¦unlimited tracking) [stat(e|us)] ", "maps");
    }

    private int pattern;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        super.init(exprs, matchedPattern, isDelayed, parseResult);
        pattern = parseResult.mark;
        return true;
    }

    @Override
    protected String getPropertyName() {
        switch (pattern) {
            case 1:
                return "locked state";
            case 2:
                return "position tracking state";
            case 3:
                return "unlimited tracking state";
        }
        return null;
    }

    @Nullable
    @Override
    public Boolean convert(MapView mapView) {
        switch (pattern) {
            case 1:
                return mapView.isLocked();
            case 2:
                return mapView.isTrackingPosition();
            case 3:
                return mapView.isUnlimitedTracking();
        }
        return null;
    }

    @Override
    public Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Nullable
    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        return mode == Changer.ChangeMode.SET ? CollectionUtils.array(Boolean.class) : null;
    }

    @Override
    public void change(Event e, @Nullable Object[] delta, Changer.ChangeMode mode) {
        Boolean b = (Boolean) delta[0];
        if (b == null) return;
        for (MapView map : getExpr().getArray(e)) {
            switch (pattern) {
                case 1:
                    map.setLocked(b);
                    break;
                case 2:
                    map.setTrackingPosition(b);
                    break;
                default:
                    map.setUnlimitedTracking(b);
            }
        }
    }
}
