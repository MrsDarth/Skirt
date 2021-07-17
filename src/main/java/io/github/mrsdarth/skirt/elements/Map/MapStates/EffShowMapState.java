package io.github.mrsdarth.skirt.elements.Map.MapStates;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.Nullable;

@Name("Show Map State")
@Description("sends a map state to a player")
@Examples("show map state {_map state} to player")
@Since("1.2.3")

public class EffShowMapState extends Effect {

    static {
        Skript.registerEffect(EffShowMapState.class,
                "(show|send) map[ ]state %mapstate% to %players%",
                "refresh %players%['[s]] view of %maps%");
    }

    private Expression<MapState> mapState;
    private Expression<MapView> mapViews;
    private Expression<Player> players;
    private boolean send;

    @Override
    protected void execute(Event event) {
        if (send) {
            MapState state = mapState.getSingle(event);
            if (state == null) return;
            players.stream(event).forEach(state::sendToPlayer);
        } else {
            MapView[] maps = mapViews.getArray(event);
            for (Player p: players.getArray(event))
                for (MapView map: maps)
                    p.sendMap(map);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "show mapstate";
    }

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        send = i == 0;
        if (send)
            mapState = (Expression<MapState>) exprs[0];
        else
            mapViews = (Expression<MapView>) exprs[0];
        players = (Expression<Player>) exprs[1];
        return true;
    }
}
