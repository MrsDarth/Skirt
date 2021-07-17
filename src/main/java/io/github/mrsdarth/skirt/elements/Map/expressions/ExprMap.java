package io.github.mrsdarth.skirt.elements.Map.expressions;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import io.github.mrsdarth.skirt.elements.Map.Renderer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.Nullable;

@Name("Map")
@Description("returns a map in a map initialise event, from an id or newly created from a world")
@Examples("set {_map} to map from player's world")
@Since("1.2.0")

public class ExprMap extends SimpleExpression {

    static {
        Skript.registerExpression(ExprMap.class, MapView.class, ExpressionType.COMBINED,
                "[the] [event-]map",
                "map [from id] %number%",
                "[create] [new] map from [world] %world%");
    }

    @Nullable
    @Override
    protected MapView[] get(Event e) {
        switch (pattern) {
            case 0:
                return CollectionUtils.array(((MapInitializeEvent) e).getMap());
            case 1:
                return (id.getSingle(e) != null) ? CollectionUtils.array(Renderer.getMap(id.getSingle(e).intValue())) : null;
            case 2:
                return (world.getSingle(e) != null) ? CollectionUtils.array(Bukkit.createMap(world.getSingle(e))) : null;
        }
        return null;
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
                }
                break;
            case 1:
                id = (Expression<Number>) exprs[0];
                break;
            default:
                world = (Expression<World>) exprs[0];
        }
        return true;
    }
}
