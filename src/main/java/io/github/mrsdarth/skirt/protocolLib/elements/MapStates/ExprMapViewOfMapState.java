package io.github.mrsdarth.skirt.protocolLib.elements.MapStates;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.*;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Map State Map")
@Description("Allows you to get or change the map of a map state to be played on different maps")
@Examples("set map of map state {_map state} to 0 # or just map from id 0")
@Since("1.2.3")
@RequiredPlugins("ProtocolLib")

public class ExprMapViewOfMapState extends PropertyExpression<MapState, Long> {

    static {
        Skript.registerExpression(ExprMapViewOfMapState.class, Long.class, ExpressionType.PROPERTY,
                "[map] id of map state[s] %mapstates%",
                "map state %mapstates%'[s] [map] id");
    }

    @Override
    protected Long @NotNull [] get(@NotNull Event e, MapState @NotNull [] source) {
        return get(source, mapState -> (long) mapState.getId());
    }

    @Override
    public @NotNull Class<? extends Long> getReturnType() {
        return Long.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "map id of map state " + getExpr().toString(e, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        setExpr((Expression<? extends MapState>) exprs[0]);
        return false;
    }

    @Override
    public @Nullable
    Class<?>[] acceptChange(Changer.@NotNull ChangeMode mode) {
        return mode == Changer.ChangeMode.SET ? CollectionUtils.array(Number.class) : null;
    }

    @Override
    public void change(@NotNull Event e, Object @Nullable [] delta, Changer.@NotNull ChangeMode mode) {
        if (ArrayUtils.isEmpty(delta) || !(delta[0] instanceof Number number)) return;
        for (MapState mapState: getExpr().getArray(e))
            mapState.getMapPacket().getIntegers().write(0, number.intValue());
    }
}
