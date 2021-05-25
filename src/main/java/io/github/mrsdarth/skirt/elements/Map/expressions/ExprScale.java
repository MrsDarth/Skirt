package io.github.mrsdarth.skirt.elements.Map.expressions;


import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class ExprScale extends SimplePropertyExpression<MapView, Number> {

    static {
        register(ExprScale.class, Number.class, "[map] (scale|zoom|size)", "map");
    }

    private MapView.Scale[] scales = MapView.Scale.values();

    private int clamp(int value, int min, int max) {
        return Math.min(max,Math.max(value, min));
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    @Nullable
    public Number convert(MapView m) {
        return Arrays.asList(scales).indexOf(m.getScale());
    }

    @Override
    protected String getPropertyName() {
        return "scale";
    }

    @Override
    public Class getReturnType() {
        return Number.class;
    }

    @Override
    public void change(Event event, Object[] delta, ChangeMode mode){
        byte change = delta == null ? 0 : ((Number) delta[0]).byteValue();
        MapView m = getExpr().getSingle(event);
        if (m != null) {
            int old = Arrays.asList(scales).indexOf(m.getScale());
            switch (mode) {
                case ADD:
                    old += change; break;
                case REMOVE:
                    old -= change; break;
                case RESET:
                    old = 0; break;
                default:
                    old = change;
            }
            m.setScale(scales[clamp(old, 0, scales.length-1)]);
        }
    }

    @Override
    public Class<?>[] acceptChange(final ChangeMode mode) {
        switch (mode) {
            case SET:
            case ADD:
            case REMOVE:
            case RESET:
                return new Class[]{Number.class};
        }
        return null;
    }

}
