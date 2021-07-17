package io.github.mrsdarth.skirt.elements.Map.MapStates;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.util.Arrays;

@Name("Map State")
@Description("returns a map state that can be sent to a player")
@Examples("set {_map states::*} to (frames of gif from file \"gifs/gif.gif\") at 0, 0 on map of player's tool")
@Since("1.2.3")

public class ExprMapState extends SimpleExpression<MapState> {

    static {
        Skript.registerExpression(ExprMapState.class, MapState.class, ExpressionType.COMBINED,
                "[(1¦partial)] map state (of|from) %mapcanv%",
                "%images% at [\\(][x[ ]]%number%,[ ][y[ ]]%number%[\\)] on %map%",
                "%player%'s current view of %map%");
    }

    private int pattern;
    private Expression<MapCanvas> canvas;
    private boolean base;
    private Expression<BufferedImage> image;
    private Expression<Number> x, y;
    private Expression<MapView> map;
    private Expression<Player> player;

    @Nullable
    @Override
    protected MapState[] get(Event event) {
        switch (pattern) {
            case 0:
                MapCanvas canvas = this.canvas.getSingle(event);
                return canvas != null ? CollectionUtils.array(new MapState(canvas, this.base)) : null;
            case 1:
                Number x = this.x.getSingle(event), y = this.y.getSingle(event);
                MapView map = this.map.getSingle(event);
                return (x == null || y == null || map == null) ? null :
                        Arrays.stream(this.image.getArray(event)).map(img -> new MapState(map, img, x.intValue(), y.intValue())).toArray(MapState[]::new);
            default:
                MapView mapView = this.map.getSingle(event);
                Player player = this.player.getSingle(event);
                return (mapView == null || player == null) ? null : CollectionUtils.array(new MapState(mapView, player));
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends MapState> getReturnType() {
        return MapState.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "map state";
    }

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        pattern = i;
        switch (i) {
            case 0:
                canvas = (Expression<MapCanvas>) exprs[0];
                base = parseResult.mark != 1;
                break;
            case 1:
                image = (Expression<BufferedImage>) exprs[0];
                x = (Expression<Number>) exprs[1];
                y = (Expression<Number>) exprs[2];
                map = (Expression<MapView>) exprs[3];
                break;
            default:
                player = (Expression<Player>) exprs[0];
                map = (Expression<MapView>) exprs[1];
        }
        return true;
    }
}
