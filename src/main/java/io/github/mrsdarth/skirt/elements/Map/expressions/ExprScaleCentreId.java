package io.github.mrsdarth.skirt.elements.Map.expressions;


import ch.njol.skript.classes.Changer.ChangeMode;
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

@Name("Map Scale / Center / Id")
@Description("returns the scale, center x or z location, or id of a map")
@Examples("set center x of map of player's tool to x location of player")
@Since("1.2.0")


public class ExprScaleCentreId extends SimplePropertyExpression<MapView, Number> {

    static {
        register(ExprScaleCentreId.class, Number.class, "(1¦[map] (scale|zoom)|cent(er|re) (2¦x|3¦z)|4¦map id)", "maps");
    }

    @SuppressWarnings("deprecation")
    public static byte scale(MapView map) {
        return map.getScale().getValue();
    }

    private static MapView.Scale[] scales = MapView.Scale.values();
    private int mark;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        super.init(exprs, matchedPattern, isDelayed, parseResult);
        mark = parseResult.mark;
        return true;
    }

    @Override
    protected String getPropertyName() {
        switch (mark) {
            case 1:
                return "scale";
            case 2:
                return "center x";
            case 3:
                return "center z";
            default:
                return "map id";
        }
    }

    @Nullable
    @Override
    public Number convert(MapView mapView) {
        switch (mark) {
            case 1:
                return scale(mapView);
            case 2:
                return mapView.getCenterX();
            case 3:
                return mapView.getCenterZ();
            default:
                return mapView.getId();
        }
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public void change(Event event, Object[] delta, ChangeMode mode) {
        if (delta == null) return;
        int change = ((Number) delta[0]).intValue();
        for (MapView m : getExpr().getArray(event)) {
            int value =
                    mark == 1 ? scale(m) :
                            mark == 2 ? m.getCenterX() : m.getCenterZ();
            switch (mode) {
                case ADD:
                    value += change;
                    break;
                case REMOVE:
                    value -= change;
                    break;
                case RESET:
                    value = 0;
                    break;
                default:
                    value = change;
            }
            switch (mark) {
                case 1:
                    m.setScale(scales[ExprMapPixel.clamp(value, 0, scales.length - 1)]);
                    break;
                case 2:
                    m.setCenterX(value);
                    break;
                default:
                    m.setCenterZ(value);
            }
        }
    }

    @Override
    public Class<?>[] acceptChange(final ChangeMode mode) {
        if (mark != 4) {
            switch (mode) {
                case SET:
                case ADD:
                case REMOVE:
                case RESET:
                    return CollectionUtils.array(Number.class);
            }
        }
        return null;
    }

}
