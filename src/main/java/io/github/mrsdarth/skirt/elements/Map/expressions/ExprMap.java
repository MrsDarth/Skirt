package io.github.mrsdarth.skirt.elements.Map.expressions;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.Nullable;

public class ExprMap extends SimpleExpression {

    static {
        Skript.registerExpression(ExprMap.class, MapView.class, ExpressionType.COMBINED,
                "[the] [event-]map",
                "map from id %number%",
                "[create] [new] map from world %world%");
    }

    @Nullable
    @Override
    @SuppressWarnings("deprecation")
    protected MapView[] get(Event event) {
        switch (pattern) {
            case 0: return new MapView[] {((MapInitializeEvent) event).getMap()};
            case 1: if (id.getSingle(event) != null) return new MapView[] {Bukkit.getMap(id.getSingle(event).intValue())};
            case 2: if (world.getSingle(event) != null) return new MapView[] {Bukkit.createMap(world.getSingle(event))};
        } return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class getReturnType() {
        return MapView.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "map";
    }

    private int pattern;
    private Expression<Number> id;
    private Expression<World> world;

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        pattern = i;
        switch (i) {
            case 0:
                if (!ScriptLoader.isCurrentEvent(MapInitializeEvent.class)) {
                    Skript.error("event-map can only be used in map initialize event", ErrorQuality.SEMANTIC_ERROR);
                    return false;
                } break;
            case 1: id = (Expression<Number>) exprs[0]; break;
            default: world = (Expression<World>) exprs[0];
        }
        return true;
    }
}
