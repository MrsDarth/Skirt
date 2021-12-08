package io.github.mrsdarth.skirt.protocolLib.elements.MapStates;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.*;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.registrations.Converters;
import ch.njol.util.Kleenean;
import io.github.mrsdarth.skirt.elements.map.Maps;
import org.bukkit.event.Event;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;

@Name("Map State")
@Description("returns a map state that can be sent to a player")
@Examples({})
@Since("1.2.3")
@RequiredPlugins("ProtocolLib")

public class ExprMapState extends SimpleExpression<MapState> {

    static {
        Skript.registerExpression(ExprMapState.class, MapState.class, ExpressionType.COMBINED,
                "map state (of|from) %mapcanvases%",
                "map state of %images% [at " + Maps.coordPattern(true) + "] on %map%");
    }

    private Expression<MapCanvas> canvasExpr;
    private Expression<BufferedImage> imageExpr;
    private Expression<Number> numberExpr1, numberExpr2;
    private Expression<MapView> mapExpr;

    private boolean isImage;

    @Override
    protected @Nullable
    MapState[] get(@NotNull Event e) {
        if (isImage) {
            MapView map = mapExpr.getSingle(e);
            Number
                    x = numberExpr1 == null ? 0 : numberExpr1.getSingle(e),
                    y = numberExpr2 == null ? 0 : numberExpr2.getSingle(e);
            return (map == null || x == null || y == null) ? null : Converters.convert(imageExpr.getArray(e), MapState.class,
                    image -> new MapState(map, image, x.intValue(), y.intValue()));
        } else
            return Converters.convert(canvasExpr.getArray(e), MapState.class, MapState::new);
    }

    @Override
    public boolean isSingle() {
        return (isImage ? imageExpr : canvasExpr).isSingle();
    }

    @Override
    public @NotNull Class<? extends MapState> getReturnType() {
        return MapState.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "map state of " + (isImage ? imageExpr.toString(e, debug) + (numberExpr1 == null || numberExpr2 == null ? "" : " at (" + numberExpr1.toString(e, debug) + ", " + numberExpr2.toString(e, debug) + ")") + " on " + mapExpr.toString(e, debug) : canvasExpr.toString(e, debug));
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        isImage = matchedPattern == 1;
        if (isImage) {
            imageExpr = (Expression<BufferedImage>) exprs[0];
            numberExpr1 = (Expression<Number>) exprs[1];
            numberExpr2 = (Expression<Number>) exprs[2];
            mapExpr = (Expression<MapView>) exprs[3];
        } else
            canvasExpr = (Expression<MapCanvas>) exprs[0];
        return true;
    }
}
